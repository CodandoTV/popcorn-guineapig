#!/usr/bin/env python3
"""Structural checker for the Popcorn Guinea Pig error report.

Validates the Markdown that `popcornParent -PerrorReportEnabled` writes to
`build/reports/popcornguineapig/errorReport.md`, the same content a GitHub Action
posts on a pull request.

Checks, per module section:
  * header and separator rows carry exactly N columns;
  * every violation row splits into exactly N cells (a `|` is a delimiter only
    when it is preceded by an even number of backslashes);
  * exactly one summary, one `Violations Found` count, one details table;
  * the count matches the number of rows and rows are numbered 1..N;
  * on reports that label sections with a Gradle project path (`:module`), the
    Module cell matches the section label;
  * no duplicate analysis sections and no unexpected lines (a raw newline inside
    a cell used to break the row);
  * backticks are not escaped (inline code in a description must render).

Exit codes: 0 = report is structurally sound, 1 = findings, 2 = usage error.
"""

from __future__ import annotations

import argparse
import sys
from dataclasses import dataclass, field

ANALYSIS_PREFIX = "## Analysis: "
ANALYSIS_SUFFIX = " Module"
VIOLATIONS_PREFIX = "- **Violations Found**:"
DETAILS_HEADER = "## Violation Details"
SUMMARY_HEADER = "### Summary"
SEPARATOR_LINE = "-----"
EXPECTED_CELLS = 6


@dataclass
class Finding:
    line: int
    message: str


@dataclass
class Section:
    label: str
    line: int
    summaries: int = 0
    violations_lines: list[tuple[int, str]] = field(default_factory=list)
    details_headers: int = 0
    table_headers: list[int] = field(default_factory=list)
    separators: int = 0
    rows: list[tuple[int, list[str]]] = field(default_factory=list)
    unexpected: list[tuple[int, str]] = field(default_factory=list)


def _trailing_pipe_is_delimiter(text: str) -> bool:
    """True when the last character is a pipe that is not escaped."""
    if not text.endswith("|"):
        return False
    backslashes = 0
    index = len(text) - 2
    while index >= 0 and text[index] == "\\":
        backslashes += 1
        index -= 1
    return backslashes % 2 == 0


def split_cells(line: str) -> list[str] | None:
    """Split a Markdown table row into cells, honouring backslash escapes."""
    text = line.strip()
    if not text.startswith("|"):
        return None

    text = text[1:]
    if _trailing_pipe_is_delimiter(text):
        text = text[:-1]

    cells: list[str] = []
    current: list[str] = []
    backslashes = 0
    for char in text:
        if char == "\\":
            current.append(char)
            backslashes += 1
            continue
        if char == "|":
            if backslashes % 2 == 1:
                current.append(char)
            else:
                cells.append("".join(current))
                current = []
        else:
            current.append(char)
        backslashes = 0
    cells.append("".join(current))
    return [cell.strip() for cell in cells]


def _is_separator(cells: list[str]) -> bool:
    return all(set(cell) <= {"-", ":"} and "-" in cell for cell in cells)


def parse_sections(lines: list[str]) -> tuple[list[Section], int | None]:
    sections: list[Section] = []
    current: Section | None = None
    first_analysis_line: int | None = None
    for index, line in enumerate(lines, start=1):
        stripped = line.strip()
        if stripped.startswith(ANALYSIS_PREFIX) and stripped.endswith(ANALYSIS_SUFFIX):
            label = stripped[len(ANALYSIS_PREFIX) : -len(ANALYSIS_SUFFIX)]
            current = Section(label=label, line=index)
            sections.append(current)
            if first_analysis_line is None:
                first_analysis_line = index
            continue
        if current is None:
            continue
        if stripped == SUMMARY_HEADER:
            current.summaries += 1
        elif stripped.startswith(VIOLATIONS_PREFIX):
            current.violations_lines.append((index, stripped))
        elif stripped == DETAILS_HEADER:
            current.details_headers += 1
        elif stripped.startswith("#"):
            # A new heading that is not part of a section is unexpected.
            current.unexpected.append((index, stripped))
        elif stripped.startswith("|"):
            cells = split_cells(stripped)
            assert cells is not None
            if cells and cells[0] == "#":
                current.table_headers.append(index)
            elif _is_separator(cells):
                current.separators += 1
            else:
                current.rows.append((index, cells))
        elif (
            stripped == ""
            or stripped == SEPARATOR_LINE
            or stripped == "---"
            or stripped.startswith("- **")  # summary bullets
        ):
            continue
        else:
            current.unexpected.append((index, stripped))
    return sections, first_analysis_line


def check(report_path: str, expected_cells: int) -> list[Finding]:
    with open(report_path, encoding="utf-8") as report_file:
        text = report_file.read()

    if not text.strip():
        print("empty report: no violations to check")
        return []

    lines = text.splitlines()
    findings: list[Finding] = []
    sections, _ = parse_sections(lines)

    if not sections:
        findings.append(Finding(1, "no '## Analysis:' sections found in a non-empty report"))

    labels: dict[str, int] = {}
    for section in sections:
        labels[section.label] = labels.get(section.label, 0) + 1
        where = f"section '{section.label}' (line {section.line})"

        if section.summaries != 1:
            findings.append(Finding(section.line, f"{where}: expected exactly 1 summary, found {section.summaries}"))
        if len(section.violations_lines) != 1:
            findings.append(
                Finding(
                    section.line,
                    f"{where}: expected exactly 1 'Violations Found' line, found {len(section.violations_lines)}",
                )
            )
        if section.details_headers != 1:
            findings.append(
                Finding(
                    section.line,
                    f"{where}: expected exactly 1 '{DETAILS_HEADER}' header, found {section.details_headers}",
                )
            )
        if len(section.table_headers) != 1:
            findings.append(
                Finding(
                    section.line,
                    f"{where}: expected exactly 1 table header row, found {len(section.table_headers)}",
                )
            )
        if section.separators != 1:
            findings.append(
                Finding(section.line, f"{where}: expected exactly 1 table separator row, found {section.separators}")
            )

        for line_number, raw in section.unexpected:
            findings.append(Finding(line_number, f"{where}: unexpected line {raw!r} (broken cell or stray content)"))

        expected_number = 1
        for line_number, cells in section.rows:
            if len(cells) != expected_cells:
                findings.append(
                    Finding(
                        line_number,
                        f"{where}: row has {len(cells)} cells, expected {expected_cells} "
                        f"(an unescaped '|' probably split the Description cell)",
                    )
                )
                continue
            number_cell = cells[0]
            if not number_cell.isdigit():
                findings.append(Finding(line_number, f"{where}: first cell {number_cell!r} is not a row number"))
            elif int(number_cell) != expected_number:
                findings.append(
                    Finding(line_number, f"{where}: expected row number {expected_number}, found {number_cell}")
                )
            else:
                expected_number += 1

            module_cell, description_cell = cells[1], cells[4]
            if section.label.startswith(":") and module_cell != section.label:
                findings.append(
                    Finding(
                        line_number,
                        f"{where}: Module cell {module_cell!r} does not match the section's project path",
                    )
                )
            if "\\`" in description_cell:
                findings.append(
                    Finding(line_number, f"{where}: description escapes a backtick, inline code will not render")
                )

        if section.violations_lines:
            line_number, violations_line = section.violations_lines[0]
            try:
                declared = int(violations_line.split(":", 1)[1].strip())
            except (IndexError, ValueError):
                findings.append(Finding(line_number, f"{where}: cannot read the violation count from {violations_line!r}"))
            else:
                if declared != len(section.rows):
                    findings.append(
                        Finding(
                            line_number,
                            f"{where}: declares {declared} violation(s) but the table has {len(section.rows)} row(s)",
                        )
                    )

    for label, count in labels.items():
        if count > 1:
            findings.append(
                Finding(
                    next(section.line for section in sections if section.label == label),
                    f"module '{label}' has {count} analysis sections; exactly one is expected",
                )
            )

    if not findings:
        total_rows = sum(len(section.rows) for section in sections)
        print(f"report is structurally sound: {len(sections)} module section(s), {total_rows} violation row(s)")
    return findings


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(description="Check the structure of a Popcorn Guinea Pig error report.")
    parser.add_argument("--report", required=True, help="path to errorReport.md")
    parser.add_argument(
        "--expected-columns",
        type=int,
        default=EXPECTED_CELLS,
        help=f"number of table columns expected (default: {EXPECTED_CELLS})",
    )
    args = parser.parse_args(argv)

    if args.expected_columns < 2:
        print("--expected-columns must be at least 2", file=sys.stderr)
        return 2

    try:
        findings = check(args.report, args.expected_columns)
    except OSError as error:
        print(f"cannot read report: {error}", file=sys.stderr)
        return 2

    if not findings:
        return 0

    for finding in findings:
        print(f"line {finding.line}: {finding.message}")
    print(f"{len(findings)} finding(s)")
    return 1


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))

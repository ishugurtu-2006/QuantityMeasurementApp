Quantity Measurement — Java
A progressive Java implementation of a Quantity Measurement system that supports length and weight unit comparisons, conversions, and additions across multiple units.

Project Structure
quantity_measurement_fixed/
├── UC1.java   – Feet equality
├── UC2.java   – Feet & Inches same-unit equality
├── UC3.java   – Cross-unit length equality (feet ↔ inches)
├── UC4.java   – Extended units: Yards & Centimeters
├── UC5.java   – Unit conversion utility (QuantityLengthConverter)
├── UC6.java   – Addition of lengths (result in first operand's unit)
├── UC7.java   – Addition with explicit target unit
├── UC8.java   – Refactored: conversion logic fully inside LengthUnit enum
└── UC9.java   – Weight measurement (Kilogram, Gram, Pound)
Use Cases
UC1 — Feet Equality
Compare two Feet objects by value using proper equals() and hashCode().

Feet a = new Feet(1.0);
Feet b = new Feet(1.0);
a.equals(b); // true
UC2 — Same-Unit Equality (Feet & Inches)
Compare feet-to-feet or inches-to-inches. No cross-unit comparison.

UC2.areEqualFeet(1.0, 1.0);    // true
UC2.areEqualInches(1.0, 2.0);  // false
UC3 — Cross-Unit Length Equality
Compare lengths across feet and inches by normalizing to a common base (feet).

UC3.areEqual(1.0, "feet", 12.0, "inches"); // true
UC3.areEqual(1.0, "feet",  2.0, "inches"); // false
UC4 — Extended Length Units
Adds support for Yards and Centimeters alongside feet and inches.

UC4.areEqual(1.0, "YARDS",  3.0, "FEET");   // true
UC4.areEqual(1.0, "YARDS", 36.0, "INCHES"); // true
UC5 — Unit Conversion Utility
QuantityLengthConverter.convert(value, from, to) converts any supported length unit to another.

QuantityLengthConverter.convert(1.0, LengthUnit.FEET, LengthUnit.INCHES); // 12.0
QuantityLengthConverter.convert(1.0, LengthUnit.YARDS, LengthUnit.FEET);  // 3.0
UC6 — Length Addition (Implicit Target Unit)
Add two quantities; result is expressed in the unit of the first operand.

QuantityLength.add(new QuantityLength(1.0, FEET), new QuantityLength(12.0, INCHES));
// → 2.0 FEET

QuantityLength.add(new QuantityLength(12.0, INCHES), new QuantityLength(1.0, FEET));
// → 24.0 INCHES
UC7 — Length Addition (Explicit Target Unit)
Add two quantities and specify the desired output unit.

QuantityLength.add(new QuantityLength(1.0, FEET), new QuantityLength(12.0, INCHES), YARDS);
// → 0.6667 YARDS

QuantityLength.add(new QuantityLength(1.0, INCHES), new QuantityLength(1.0, INCHES), CENTIMETERS);
// → 5.08 CENTIMETERS
UC8 — Refactored Enum-Driven Conversion
All conversion math (convertToBaseUnit, convertFromBaseUnit) lives inside the LengthUnit enum. QuantityLength delegates entirely to the enum — no raw multiplication factors in the class body.

new QuantityLength(30.48, CENTIMETERS).equals(new QuantityLength(1.0, FEET)); // true
new QuantityLength(1.0, YARDS).convertTo(FEET); // 3.0 FEET
UC9 — Weight Measurement
Supports Kilogram, Gram, and Pound with equality, conversion, and addition.

new QuantityWeight(1.0, KILOGRAM).equals(new QuantityWeight(1000.0, GRAM)); // true

new QuantityWeight(500.0, GRAM)
    .add(new QuantityWeight(500.0, GRAM), KILOGRAM); // → 1.0 KG
Supported Units
Length
Unit	Enum Constant	Relative to Feet
Feet	FEET	1.0
Inches	INCHES	1/12 ft
Yards	YARDS	3.0 ft
Centimeters	CENTIMETERS	1/30.48 ft
Weight
Unit	Enum Constant	Relative to Kilogram
Kilogram	KILOGRAM	1.0
Gram	GRAM	1/1000 kg
Pound	POUND	0.45359237 kg
Design Principles
Base-unit normalization — all comparisons convert values to a common base unit (feet for length, kilograms for weight) before comparing, so cross-unit equality is always correct.
Floating-point safety — equality uses Double.compare() or Math.abs(a - b) <= EPSILON instead of ==.
equals / hashCode contract — both are always overridden together and kept consistent.
Immutability — all quantity classes are immutable (final fields, no setters).
Enum-driven conversion (UC8+) — conversion factors belong to the enum, not scattered across classes.
How to Compile and Run
Compile all files
javac quantity_measurement_fixed/*.java
Run a specific use case
java -cp quantity_measurement_fixed UC1
java -cp quantity_measurement_fixed UC2
# ... and so on up to UC9
Note: Each UC file is self-contained with its own main() method demonstrating the feature.

Requirements
Java 17 or later (uses pattern matching instanceof and switch expressions)
No external dependencies for UC1–UC9 core files
JUnit 5 is referenced in UC5–UC7 test specs (not required to run the main() demos)
Bugs Fixed (from original document)
File	Issue
UC1–UC4	All outer classes were named QuantityMeasurementApp instead of matching the filename
UC4	areEqual() was an instance method called from static main()
UC4	main() referenced a non-existent QuantityMeasurementApp class
UC5	QuantityLengthConverter and toFeetFactor() were referenced but never defined
UC6–UC7	getValue(), getUnit(), and static add() were missing from QuantityLength
UC8	Critical: CENTIMETERS factor was 30.48 (cm per foot) but the field means feet per cm — should be 1.0/30.48
UC9	Heading typo (#U9), nearlyEqual() body was truncated, missing return false in equals()

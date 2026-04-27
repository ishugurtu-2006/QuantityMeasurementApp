import java.util.Objects;

/**
 * UC4: Extended unit support — FEET, INCHES, YARDS, CENTIMETERS.
 *
 * Fixes:
 *  - Outer class renamed QuantityMeasurementApp -> UC4
 *  - areEqual() made static (was instance method, but main() is static)
 *  - parseUnit() made static accordingly
 *  - main() now instantiates UC4 correctly (removed reference to non-existent QuantityMeasurementApp)
 *  - CENTIMETERS factor: 0.0328084 ft/cm is correct (kept as-is)
 */
public class UC4 {

    // Common base unit: FEET
    public enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(0.0328084); // 1 cm ≈ 0.0328084 feet

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        public double toFeet(double value) {
            return value * toFeetFactor;
        }
    }

    public static class QuantityLength {
        private final double feetValue;

        public QuantityLength(Object value, LengthUnit unit) {
            if (value == null) {
                throw new IllegalArgumentException("Value cannot be null");
            }
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            double numericValue = parseToDouble(value);
            this.feetValue = unit.toFeet(numericValue);
        }

        private static double parseToDouble(Object value) {
            if (value instanceof Number n) {
                return n.doubleValue();
            }
            if (value instanceof String s) {
                String trimmed = s.trim();
                try {
                    return Double.parseDouble(trimmed);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Value must be numeric: " + value, e);
                }
            }
            throw new IllegalArgumentException("Value must be numeric (Number or numeric String)");
        }

        public double toFeet() {
            return feetValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof QuantityLength other)) return false;
            return Double.compare(this.feetValue, other.feetValue) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(Double.doubleToLongBits(feetValue));
        }
    }

    // FIX: made static so it can be called from static main()
    public static boolean areEqual(Object value1, String unit1, Object value2, String unit2) {
        LengthUnit u1 = parseUnit(unit1);
        LengthUnit u2 = parseUnit(unit2);
        QuantityLength q1 = new QuantityLength(value1, u1);
        QuantityLength q2 = new QuantityLength(value2, u2);
        return q1.equals(q2);
    }

    // FIX: made static accordingly
    private static LengthUnit parseUnit(String unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        try {
            return LengthUnit.valueOf(unit.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported unit: " + unit, e);
        }
    }

    public static void main(String[] args) {
        // FIX: was "new QuantityMeasurementApp()" — now calls static areEqual directly
        System.out.println("1 YARD  == 3 FEET   : " + areEqual(1.0, "YARDS", 3.0, "FEET"));           // true
        System.out.println("1 YARD  == 36 INCHES: " + areEqual(1.0, "YARDS", 36.0, "INCHES"));        // true
        System.out.println("2 CM    == 0.787402 INCHES: " + areEqual(2.0, "CENTIMETERS", 0.787402, "INCHES")); // ~true
    }
}

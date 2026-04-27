import java.util.Objects;

/**
 * UC3: Cross-unit length equality (feet ↔ inches).
 * 1 foot == 12 inches, compared via conversion to a common base unit.
 *
 * Fix: outer class renamed from QuantityMeasurementApp -> UC3.
 */
public class UC3 {

    public static boolean areEqual(Object value1, String unit1, Object value2, String unit2) {
        QuantityLength3 q1 = new QuantityLength3(value1, unit1);
        QuantityLength3 q2 = new QuantityLength3(value2, unit2);
        return q1.equals(q2);
    }

    public static void main(String[] args) {
        System.out.println("1 foot  == 12 inches ? " + areEqual(1.0, "feet",   12.0, "inches")); // true
        System.out.println("1 inch  ==  1 inch   ? " + areEqual(1.0, "inch",    1.0, "inches")); // true
        System.out.println("1 foot  ==  2 inches ? " + areEqual(1.0, "feet",    2.0, "inches")); // false
    }
}

enum LengthUnit3 {
    FEET("feet", "foot", 1.0),
    INCHES("inches", "inch", 1.0 / 12.0);

    private final String[] aliases;
    private final double toFeetFactor; // value_in_unit * factor = value_in_feet

    LengthUnit3(String alias1, String alias2, double toFeetFactor) {
        this.aliases = new String[]{alias1, alias2};
        this.toFeetFactor = toFeetFactor;
    }

    public double toFeet(double valueInThisUnit) {
        return valueInThisUnit * toFeetFactor;
    }

    public static LengthUnit3 fromString(String rawUnit) {
        if (rawUnit == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        String u = rawUnit.trim().toLowerCase();
        for (LengthUnit3 unit : values()) {
            for (String alias : unit.aliases) {
                if (u.equals(alias.toLowerCase())) {
                    return unit;
                }
            }
        }
        throw new IllegalArgumentException("Unsupported unit: " + rawUnit);
    }
}

final class QuantityLength3 {
    private final double value;
    private final LengthUnit3 unit;

    public QuantityLength3(Object value, String unit) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.value = parseNumeric(value);
        this.unit = LengthUnit3.fromString(unit);
    }

    private static double parseNumeric(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String s) {
            String trimmed = s.trim();
            if (trimmed.isEmpty()) {
                throw new IllegalArgumentException("Value cannot be empty");
            }
            try {
                return Double.parseDouble(trimmed);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Value must be numeric: " + value, e);
            }
        }
        throw new IllegalArgumentException("Value must be a Number or numeric String: " + value);
    }

    public double toFeet() {
        return unit.toFeet(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuantityLength3 other)) return false;
        return Double.compare(this.toFeet(), other.toFeet()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Double.valueOf(toFeet()));
    }

    @Override
    public String toString() {
        return value + " " + unit.name().toLowerCase();
    }
}

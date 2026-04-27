import java.util.Objects;

/**
 * UC2: Same-unit equality for Feet and Inches.
 * No cross-unit comparison — that is introduced in UC3.
 *
 * Fixes:
 *  - Outer class renamed from QuantityMeasurementApp -> UC2
 *  - import statement was embedded in a comment; moved to top of file
 *  - Helper classes renamed Feet2/Inches2 to avoid conflicts if compiled
 *    alongside other UC files in the same directory
 */
public class UC2 {

    public static boolean areEqualFeet(Object v1, Object v2) {
        Feet2 f1 = new Feet2(v1);
        Feet2 f2 = new Feet2(v2);
        return f1.equals(f2);
    }

    public static boolean areEqualInches(Object v1, Object v2) {
        Inches2 i1 = new Inches2(v1);
        Inches2 i2 = new Inches2(v2);
        return i1.equals(i2);
    }

    public static void main(String[] args) {
        System.out.println("1.0 inch == 1.0 inch : " + areEqualInches(1.0, 1.0)); // true
        System.out.println("1.0 ft   == 1.0 ft   : " + areEqualFeet(1.0, 1.0));   // true
        System.out.println("1.0 inch == 2.0 inch : " + areEqualInches(1.0, 2.0)); // false
        System.out.println("1.0 ft   == 2.0 ft   : " + areEqualFeet(1.0, 2.0));   // false
    }
}

final class Feet2 {
    private final double value;

    public Feet2(Object input) {
        this.value = parseNumeric(input);
    }

    private static double parseNumeric(Object input) {
        if (input == null) {
            throw new IllegalArgumentException("Input must be numeric, but was null");
        }
        if (input instanceof Number) {
            return ((Number) input).doubleValue();
        }
        if (input instanceof String s) {
            try {
                return Double.parseDouble(s.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Input must be numeric: " + input, e);
            }
        }
        throw new IllegalArgumentException("Input must be numeric: " + input);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        Feet2 other = (Feet2) o;
        return Double.compare(this.value, other.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), value);
    }
}

final class Inches2 {
    private final double value;

    public Inches2(Object input) {
        this.value = parseNumeric(input);
    }

    private static double parseNumeric(Object input) {
        if (input == null) {
            throw new IllegalArgumentException("Input must be numeric, but was null");
        }
        if (input instanceof Number) {
            return ((Number) input).doubleValue();
        }
        if (input instanceof String s) {
            try {
                return Double.parseDouble(s.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Input must be numeric: " + input, e);
            }
        }
        throw new IllegalArgumentException("Input must be numeric: " + input);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        Inches2 other = (Inches2) o;
        return Double.compare(this.value, other.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), value);
    }
}

//Name : Rishabh Singh
//Class : CSE 123
//Date : 11/22/24
//Project : C3
//TA : Kavya Nair
import java.util.*;

// This class represents a weather instance storing information about the temperature, humidity
// and windspeed of that instance. 
public class Weather implements Classifiable {

    public static final Set<String> FEATURES = Set.of("temperature", "humidity", "windspeed");

    public static final int TEMPCOL = 3;

    public static final int HUMIDITYCOL = 5;

    public static final int WINDSPEEDCOL = 6;

    public static final double THRESHOLD = 0.3;
    
    private List<String> content;

    //Behavior : Constructs a new Weather instance from the provided input String 
    //Exceptions : None.
    //Returns : None.
    // Parameters : input - (should be non-null), properly formatted comma-separated string
    //              containing weather data.

    public Weather (String input) { 
        content = List.of(input.split(","));
    }

    // Behavior : Creates and returns a Classifiable object from the provided row of Weather 
    //            data
    // Exceptions : None.
    // Returns : Classifiable Weather object with temperature, humidity and windspeed.
    // Parameters : List<String> row of weather data, should be non-null.

    public static Classifiable toClassifiable(List<String> row) {
        return new Weather(row.get(TEMPCOL) + "," + row.get(HUMIDITYCOL) + "," 
            + row.get(WINDSPEEDCOL));
    }

    // Behavior : Returns features present within Weather instance that can be used in 
    //            classification
    // Exceptions : None.
    // Returns : None.
    // Parameters : None. 
    public Set<String> getFeatures() {
        return FEATURES;
    }

    // Behavior : Returns the numeric value for the provided feature
    // 'feature' should be non-null
    // Exceptions : Throws IllegalArgumentException
    //      If feature not contained within getFeatures()
    // Returns : double (value of feature of this row).
    // Parameters : String feature - feature desisred. 
    public double get(String feature) {
        if(!FEATURES.contains(feature)) {
            throw new IllegalArgumentException(
                    String.format("Invalid feature [%s], not within possible features [%s]",
                                  feature, FEATURES.toString()));
        }

        if(feature.equals("temperature")) {
            return Double.parseDouble(content.get(0));
        }
        else if(feature.equals("humidity")) {
            return Double.parseDouble(content.get(1));
        }
        else {
            return Double.parseDouble(content.get(2));
        }
        
    }   

    // Behavior : Returns a split of the midpoint of two provided data points, this and other.
    // Exceptions : Throws IllegalArgumentException if other isn't an instance of the 
    //              Weather class
    // Returns : Split object - representing the midpoint between two classifiables.
    // Parameters : other - other classifiable to compare to, should not be null. 
    public Split partition(Classifiable other) {

        if (!(other instanceof Weather)) {
            throw new IllegalArgumentException("Provided 'other' not instance of Weather.java");
        }

        Weather otherWeather = (Weather) other;
        List<String> prioritizedFeatures = List.of("temperature", "humidity", "windspeed");
    
        for (String feature : prioritizedFeatures) {
            double thisValue = this.get(feature);
            double otherValue = otherWeather.get(feature);
            double difference = Math.abs(thisValue - otherValue);
    
            if (difference > THRESHOLD) {
                double midpoint = Split.midpoint(thisValue, otherValue);
                return new Split(feature, midpoint);
            }
        }
        double midpoint = Split.midpoint(otherWeather.get("temperature"),
                         this.get("temperature"));

        
        return new Split("temperature", midpoint);
        

    }




}

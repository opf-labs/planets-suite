/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.services.characterisation.xcl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import eu.planets_project.pp.plato.model.measurement.Metric;
import eu.planets_project.pp.plato.model.scales.BooleanScale;
import eu.planets_project.pp.plato.model.scales.FloatRangeScale;
import eu.planets_project.pp.plato.model.scales.FloatScale;
import eu.planets_project.pp.plato.model.scales.IntegerScale;
import eu.planets_project.pp.plato.model.scales.PositiveFloatScale;
import eu.planets_project.pp.plato.model.scales.PositiveIntegerScale;
import eu.planets_project.pp.plato.model.scales.Scale;
import eu.planets_project.pp.plato.model.values.BooleanValue;
import eu.planets_project.pp.plato.model.values.FloatRangeValue;
import eu.planets_project.pp.plato.model.values.FloatValue;
import eu.planets_project.pp.plato.model.values.IntRangeValue;
import eu.planets_project.pp.plato.model.values.IntegerValue;
import eu.planets_project.pp.plato.model.values.OrdinalValue;
import eu.planets_project.pp.plato.model.values.PositiveFloatValue;
import eu.planets_project.pp.plato.model.values.PositiveIntegerValue;
import eu.planets_project.pp.plato.model.values.Value;
import eu.planets_project.pp.plato.model.values.YanValue;

public class MetricToScaleMapping implements Serializable {
    private static final long serialVersionUID = -3331419365251890899L;
    
    /**
     * Maps {@link Metric#getMetricId() XCLMetrics} to specific {@link Scale scales}.
     */
    private static Map<String, Scale> metricToScaleMap;

    private static Map<String, String> metricNameToIdMap;
    
    public static String getMetricId(String name) {
        return metricNameToIdMap.get(name);
    }
    public static Scale getScale(String metric) {
        return metricToScaleMap.get(metric);
    }
    
    /**
     * Transforms the cprResult value to the corresponding {@link Value} in Plato
     * and set it in <param>value</param> 
     * {@link OrdinalValue ordinal} and {@link YanValue yes/acceptable/no} values are not supported
     * 
     * @param cprResult
     * @param value
     */
    public static void setValueFromCprString(String cprResult, Value value) {
        if (value instanceof BooleanValue) {
            if ("true".equals(cprResult))
               ((BooleanValue)value).setValue("Yes");
            else
                ((BooleanValue)value).setValue("No");                
        } else if (value instanceof IntegerValue)
            ((IntegerValue)value).setValue(Integer.parseInt(cprResult));
        else if (value instanceof PositiveIntegerValue)
            ((PositiveIntegerValue)value).setValue(Integer.parseInt(cprResult));
        else if (value instanceof IntRangeValue)
            ((IntRangeValue)value).setValue(Integer.parseInt(cprResult));
        else if (value instanceof FloatValue)
            ((FloatValue)value).setValue(Double.parseDouble(cprResult));
        else if (value instanceof PositiveFloatValue)
            ((PositiveFloatValue)value).setValue(Double.parseDouble(cprResult));
        else if (value instanceof FloatRangeValue)
            ((FloatRangeValue)value).setValue(Double.parseDouble(cprResult));
    }

    static {
        metricNameToIdMap = new HashMap<String, String>();
        
        Scale scale = null;
        metricToScaleMap = new HashMap<String, Scale>();
        /* 
         * equal:
         * 
         * simple comparison of two values (A, B) of any type on equality. Type
         * of output value: Boolean (true, false).
         */
        metricToScaleMap.put("1", new BooleanScale());        
        metricNameToIdMap.put("equal", "1");

        /*
         * levenshteinDistance:
         * 
         * The Levenshtein distance (syn. Edit distance) is a distance measure
         * for strings. Two strings are compared with respect to the three basic operations
         * insert, delete and replace in order to transform string A into string B. The
         * value for this metric is the number of operations needed for transformation.:
         */
        metricToScaleMap.put("15", new PositiveIntegerScale());
        metricNameToIdMap.put("levenshteinDistance", "15");
        
        
        /*
         * intDiff:
         * 
         * Arithmetical difference of two integer values (A - B):
         */ 
        metricToScaleMap.put("2", new IntegerScale());
        metricNameToIdMap.put("intDiff", "2");
        /*
         * intRatio:
         * 
         * Quotient of two integer values (A / B). 
         */
        metricToScaleMap.put("6", new FloatScale());
        metricNameToIdMap.put("intRatio", "6");
        /*
         * percDeviation:
         * 
         * Percental deviation of two values (A,B) of type integer or rational,
         * according to the equation: PercDev= d/A*100. A is the source value, d is the
         * difference of values B and A (B-A). The output value indicates the percental
         * deviation of value B from value A. An output value of PercDev=0.0 indicates
         * equal values.
         */        
        scale = new FloatRangeScale();
        ((FloatRangeScale)scale).setRestriction("-100.0/100.0");        
        metricToScaleMap.put("10", scale);
        metricNameToIdMap.put("percDeviation", "10");
         /* 
          * hammingDistance:
          * 
          * The hamming distance counts the number of non-corresponding symbols
          * of two ordered sequences. The sequences must have equal length.
          */
         metricToScaleMap.put("11", new PositiveIntegerScale());
         metricNameToIdMap.put("hammingDistance", "11");
         /* 
          * simpleMatchCoefficientN:
          * 
          * Simple match coefficient is a distance measure that uses the hamming
          * distance. In addition, the ratio of non-corresponding values (hamming distance)
          * to the total number of compared pairs of values is calculated. N indicates the
          * n-grams used for comparison. Default setting is N=1. The result is a rational
          * number from range 0.0 to 1.0, indicating maximum similarity (0.0) to minimum
          * (1.0).
          */         
          scale = new PositiveFloatScale();
          ((PositiveFloatScale)scale).setRestriction("1.0");        
          metricToScaleMap.put("12", scale);
          metricNameToIdMap.put("simpleMatchCoefficientN", "12");
          /*
           * simpleMatchCoefficientExtN:
           * 
           * This measure is an extension of the simpleMatchCoefficient. In
           * addition it is presumed that the order of the values of the sets is not fixed.
           * Thus the hamming distance is not restricted to the positions of the single
           * values within the set. N indicates the n-grams used for comparison. Default
           * setting is N=1.
           */          
          scale = new PositiveFloatScale();
          ((PositiveFloatScale)scale).setRestriction("1.0");        
          metricToScaleMap.put("14", scale);
          metricNameToIdMap.put("simpleMatchCoefficientExtN", "14");
          /*
           * euclidDistance:
           *  
           * Euclidian distance (syn. L2 distance) is a distance measure; the
           * square root of the sum of the single squred differnces of corresponding values
           * of two sets.
           */
          metricToScaleMap.put("17", new PositiveFloatScale());
          metricNameToIdMap.put("euclidDistance", "17");
          
          /*
           * RMSE:
           * 
           * Root mean squared error is a widely used measure for deviation. It is
           * calculated as the square root of the quotient of the summed up squared
           * diffeences of corresponding values of two sets and as denominator the number of
           * values (values in both sets must have the same number). 
           */
          metricToScaleMap.put("20", new PositiveFloatScale());
          metricNameToIdMap.put("RMSE", "20");
          
    }
    
   

}

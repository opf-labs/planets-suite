/**
 * 
 */
package eu.planets_project.tb.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import au.com.bytecode.opencsv.CSVWriter;

import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.model.exec.MeasurementRecordImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ExperimentUtils {
    /** */
    private static PlanetsLogger log = PlanetsLogger.getLogger(ExperimentUtils.class);

    /** */
    public static enum DATA_FORMAT { CSV };

    /**
     * 
     * @param os
     * @param expId
     * @throws IOException 
     */
    public static void outputResults( OutputStream os, String expId, DATA_FORMAT format ) throws IOException {
        log.info("Writing out experiment "+expId+" as "+format);

        Writer out = new BufferedWriter( new OutputStreamWriter( os, "UTF-8" ) );
        CSVWriter writer = new CSVWriter(out);

        long id = Long.parseLong(expId);

        ExperimentPersistencyRemote edao = ExperimentPersistencyImpl.getInstance();
        Experiment exp = edao.findExperiment(id);

        // The string array 
        String sa[] = new String[8];
        sa[0] = "Name";
        sa[1] = "Run #";
        sa[2] = "Date";
        sa[3] = "Digital Object #";
        sa[4] = "Digital Object Source";
        sa[5] = "Stage";
        sa[6] = "Property Identifier";
        sa[7] = "Property Value";
        // write the headers out:
        writer.writeNext(sa);
        
        // Loop through:
        int bi = 1;
        for( BatchExecutionRecordImpl batch : exp.getExperimentExecutable().getBatchExecutionRecords() ) {
            // log.info("Found batch... "+batch);
            int doi = 1;
            for( ExecutionRecordImpl exr : batch.getRuns() ) {
                // log.info("Found Record... "+exr+" stages: "+exr.getStages());
                if( exr != null && exr.getStages() != null ) {
                    for( ExecutionStageRecordImpl exsr : exr.getStages() ) {
                        // log.info("Found Stage... "+exsr);
                        for( MeasurementRecordImpl m : exsr.getMeasurements() ) {
                            // log.info("Looking at result for property "+m.getIdentifier());
                            sa[0] = exp.getExperimentSetup().getBasicProperties().getExperimentName();
                            sa[1] = "" + bi;
                            sa[2] = batch.getStartDate().getTime().toString();
                            sa[3] = "" + doi;
                            sa[4] = exr.getDigitalObjectSource();
                            sa[5] = exsr.getStage();
                            sa[6] = m.getIdentifier();
                            sa[7] = m.getValue();
                            // Write out CSV:
                            writer.writeNext( sa );
                        }
                    }
                }
                // Increment, for the next DO.
                doi++;
                out.flush();
            }
            // Increment to the next batch:
            bi++;
        }

    }
}

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
package at.tuwien.minimee.admin;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.jboss.annotation.ejb.cache.Cache;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.faces.FacesMessages;

import at.tuwien.minimee.migration.engines.IMigrationEngine;
import at.tuwien.minimee.model.Tool;
import at.tuwien.minimee.model.ToolConfig;
import at.tuwien.minimee.registry.ToolRegistry;
import eu.planets_project.pp.plato.model.ToolExperience;
import eu.planets_project.pp.plato.services.action.MigrationResult;
import eu.planets_project.pp.plato.util.FileUtils;
import eu.planets_project.pp.plato.util.PlatoLogger;

/**
 * provides administrative utilities for the MiniMEE engines and registry
 * @author Christoph Becker
 */
@Stateful
@Scope(ScopeType.APPLICATION)
@Name("miniMeeAdmin")
@Synchronized
@Cache(org.jboss.ejb3.cache.NoPassivationCache.class)
public class MiniMeeAdminAction implements IMiniMeeAdmin, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -9187810681679223092L;

    private static final Log log = PlatoLogger
            .getLogger(MiniMeeAdminAction.class);

    @PersistenceContext
    EntityManager em;

    @Out
    private HashMap<String, String> initResults = new HashMap<String, String>();

    @Out
    private List<String> configs = new ArrayList<String>();

    /**
     * string pointing to a directory from which to load the registry config.
     * @see #reloadRegistryFromPath()
     */
    @In (required=false)
    private String localPath;
    
    /**
     * performs a verification test that checks all configured components
     * and configurations for proper operation.
     * Output is logged, not returned anywhere else.
     */
    public String verifySetup() {
        log.info("INITIALISING AND VERIFYING TOOL REGISTRY NOW...");
        initResults.clear();
        configs.clear();

        for (Tool tool : ToolRegistry.getInstance().getTools()) {
            for (ToolConfig config : tool.getConfigs()) {
                log.info("*** CHECKING CONFIG: " + config.getUrl());
                configs.add(config.getUrl());
                String initDir = config.getInitialisationDir();

                IMigrationEngine engine = ToolRegistry.getInstance()
                        .getAllEngines().get(config.getEngine());

                if (initDir == null) {
                    log.error("No initDir for " + config.getUrl());
                    initResults.put(config.getUrl(), "no init directory specified");
                    continue;
                }
                File directory = new File(initDir);
                if (directory.isDirectory()) {
                    try {
                        for (File f : directory.listFiles()) {
                            log.debug("testing " + config.getUrl() + " with file "
                                    + f.getName());
                            byte[] data = FileUtils.getBytesFromFile(f);
                            MigrationResult r = engine.migrate(data, "minimee/"+ config.getUrl(), "");
                            if (!r.isSuccessful()) {
                                log.warn(r.getReport());
                                initResults.put(config.getUrl(), "FAILED: "
                                        + r.getReport());
                            } else {
                                initResults.put(config.getUrl(), "SUCCESS");
                            }
                        }
                    } catch (Exception e) {
                        log.error("ERROR IN CONFIG!" + e.getMessage(),e);
                        initResults.put(config.getUrl(), "FAILED: " + e.getMessage());
                    }
                } else {
                    log.warn("Init dir " + directory + " is not a directory.");
                }
            }
        }
        for (ToolConfig c: ToolRegistry.getInstance().getAllToolConfigs().values()) {
            ToolExperience ex = ToolRegistry.getInstance().getEb().getToolExperience(c.getName());
            log.debug("Startup time of " +c.getName()+": "+ex.getStartupTime());
        }
        
        return null;
    }

    /**
     * does what it seems - it reloads the MiniMEE tool registry from
     * the configuration file
     * @link {@link ToolRegistry#reload()}
     */
    public String reloadRegistry() {
        ToolRegistry.reload();
        return null;
    }

    @Remove
    @Destroy
    public void destroy() {
    }

    /**
     * performs a benchmark calculation, reports it to the UI
     * and sets it in the {@link ToolRegistry}
     * {@link ToolRegistry#calculateBenchmarkScore()}
     */
    public String benchmark() {
        double score = ToolRegistry.getInstance().calculateBenchmarkScore();
        FacesMessages.instance().add(FacesMessage.SEVERITY_INFO,
                "Ladies and gentlemen, this registry has a score of....."+score);
        ToolRegistry.getInstance().setBenchmarkScore(score);        
        return null;
    }

    /**
     * reloads the {@link ToolRegistry} from {@link #localPath}
     * 
     */
    public String reloadRegistryFromPath() {
        if (localPath == null || "".equals(localPath)) {
            FacesMessages.instance().add(FacesMessage.SEVERITY_INFO,
                    "Please provide a local path name to the XML file");
        } else {
            ToolRegistry.reload(localPath);
        }
        return null;
    }
}

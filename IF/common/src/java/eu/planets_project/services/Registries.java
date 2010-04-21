/* NOTE: This class is currently commented out as with our current build setup 
 * we cannot reference classes outside of IF/common from within IF/common. With
 * the new project setup on SF, this class could provide a central place of access 
 * to the Planets registries. */

//package eu.planets_project.services;
//
//import eu.planets_project.ifr.core.servreg.api.ServiceRegistry;
//import eu.planets_project.ifr.core.servreg.api.ServiceRegistryFactory;
//import eu.planets_project.ifr.core.storage.api.DataRegistry;
//import eu.planets_project.ifr.core.storage.api.DataRegistryFactory;
//import eu.planets_project.ifr.core.techreg.formats.FormatRegistry;
//import eu.planets_project.ifr.core.techreg.formats.FormatRegistryFactory;
//
///**
// * Factory methods for Planets registries.
// * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
// */
//public final class Registries {
//    
//    private Registries() { /* enforce non-instantiability */ }
//
//    /**
//     * @return A local Planets service registry
//     * @see ServiceRegistryFactory
//     */
//    public static ServiceRegistry getServiceRegistry() {
//        return ServiceRegistryFactory.getServiceRegistry();
//    }
//
//    /**
//     * @return A local Planets format registry
//     * @see FormatRegistryFactory
//     */
//    public static FormatRegistry getFormatRegistry() {
//        return FormatRegistryFactory.getFormatRegistry();
//    }
//    
//    /**
//     * @return A local Planets data registry
//     * @see DataRegistryFactory
//     */
//    public static DataRegistry getDataRegistry() {
//        return DataRegistryFactory.getDataRegistry();
//    }
//}

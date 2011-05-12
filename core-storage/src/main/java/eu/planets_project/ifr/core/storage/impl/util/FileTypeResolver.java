package eu.planets_project.ifr.core.storage.impl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/**
    This utility class maps filename extensions to mime-types.
    
    The class parses an xml file of the following form that contains the mimetype mappings:
    
    
    <?xml version="1.0" encoding="ISO-8859-1"?>
    <planets:document xmlns:planets="http://www.planets-project.eu/xml/ns/planets/core/mimetypes">
      <planets:mime-mapping>
        <planets:extension>abs</planets:extension>
        <planets:mime-type>audio/x-mpeg</planets:mime-type>
        <planets:PrettyPrint-MIME-type>abs</planets:PrettyPrint-MIME-type>
      </planets:mime-mapping>
     [...]
     </planets:document>

    This class is implemented as Singleton.
    
    
    MimeTypes: RFC 2045, 2046 and 2070
    
    MediaTypes:
        text = textfiles
        image = images
        video = videofiles
        audio = soundfiles
        application = files, bound to a particular application
        multipart = files consisting of multiple parts
        message = messages
        model = files that represent multi-dimensional structures
        
    @author Reis Markus, ARC            
        
*/

public class FileTypeResolver {


    /** contains the mimetype mappings. 
        key: extension (as String), value: mimetype (as String) 
    */
    private Hashtable<String, String> mappings = null;
    
    /** contains the mimetype mappings. 
     key: mime-type (as String), value: prettty print mimetype (as String) 
     */
    private Hashtable<String, String> printMappings = null;

    static private FileTypeResolver myself = null;

    /**
        Constructor.
        loads mappings from the specified (property) input stream
    */
    protected FileTypeResolver() throws Exception
    {
    	mappings = new Hashtable<String, String>();
    	printMappings = new Hashtable<String, String>();

    	ClassLoader classLoader = this.getClass().getClassLoader();
    	loadMappings( classLoader.getResourceAsStream("eu/planets_project/ifr/core/storage/ext2mime-map.xml") );
    }


    /**
        Constructor.
        Tries to load the mappings from the passed stream.
    */
    protected FileTypeResolver( InputStream ftrIn ) throws Exception
    {
        mappings = new Hashtable<String, String>();
        printMappings = new Hashtable<String, String>();
        loadMappings( ftrIn );
    }
    
    /**
        Gets an instance of the FileTypeResolver.
    */
    public static FileTypeResolver instantiate() throws Exception
    {
        if ( myself == null )
            myself = new FileTypeResolver();
        return myself;
    }

    /**
        Gets an instance of the FileTypeResolver from the passed file.
    */
    public static FileTypeResolver instantiate( String ftrFile ) throws Exception
    {
        if ( myself == null ) {
            try {
                File f = new File( ftrFile );
                if ( ! f.exists() ) 
                    throw new Exception( "mimetype mapping file does not exist at "+f );
                myself = new FileTypeResolver( new FileInputStream( f ) );
            } catch ( IOException e0 ) {
                throw new Exception( "could not load mimetype mapping file !", e0 );
                }
            }
        return myself;
    }

    /**
        Gets an instance of the FileTypeResolver from the passed stream.
    */
    public static FileTypeResolver instantiate( InputStream ftrIn ) throws Exception
    {
        if ( myself == null )
            myself = new FileTypeResolver( ftrIn );
        return myself;
    }
    

    /**
        Loads extension-to-mimetype mappings from the xml contents read from the passed stream.
    */
    private void loadMappings( InputStream in ) throws Exception
    {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document d = builder.build( in );           

            Namespace ns = Namespace.getNamespace( "planets", "http://www.planets-project.eu/xml/ns/planets/core/mimetypes" );            
            JDOMXPath xpath = new JDOMXPath("/planets:Document/planets:MIME-mapping");    
            xpath.addNamespace( "planets", "http://www.planets-project.eu/xml/ns/planets/core/mimetypes" );                        
            
            Iterator<?> _mappings = xpath.selectNodes( d ).iterator();
            while ( _mappings.hasNext() ) {            
            
                Element _mapping = (Element)_mappings.next();
                String _extname = ((Element)_mapping.getChild("Extension", ns)).getText().toLowerCase();

                if (_extname == null)                 
                    throw new Exception(" unable to load extension 2 mime-type relation ");
                else ;
                String _mimetype = ((Element)_mapping.getChild("MIME-type", ns)).getText();
                if (_mimetype == null) 
                    throw new Exception(" unable to load extension 2 mime-type relation ");
                else ;  
                
                mappings.put(_extname, _mimetype.toLowerCase() );
                
                String _pretty_print_mimetype = null;
                if (_mapping.getChild("PrettyPrint-MIME-type", ns) != null)
                    _pretty_print_mimetype = ((Element)_mapping.getChild("PrettyPrint-MIME-type", ns)).getText();
                else ;
                if (_pretty_print_mimetype != null) {
                    if (printMappings.get(_mimetype.toLowerCase()) == null)
                        printMappings.put(_mimetype.toLowerCase(), _pretty_print_mimetype);
                    else ; //log.warn("Ignoring pretty print mime tyep mapping (" + _mimetype.toLowerCase() + ", " + _pretty_print_mimetype + ") - cause: a mapping for this mimetype is already defined");
                }
                else ;
                
            }
           
        } catch ( Exception e0 ) {
            throw new Exception( "unable to load obj 2 relation", e0 );
        } 
    }

    
    /**
        @returns the mime-type for the passed file or null if no mapping was found.
    */
    public String getMIMEType( File file ) 
    {
        return getMIMEType( file.getName() );
    }
    
    
    /**    
        @returns true if the passes mime-type is known by this file type resolver.
    */
    public boolean isKnownMIMEType( String _mimetype ) 
    {
        if ( _mimetype == null )
            return false;
        ArrayList<?> al = getMIMETypeList();
        return al.contains( _mimetype );
    }

    /**
        @returns the mime-type for the passed filename or null if no mapping was found.
    */
    public String getMIMEType( String fileName ) 
    {
        String extension = fileName.substring( fileName.lastIndexOf('.') + 1 ); 
        return mappings.get( extension.toLowerCase() );
    }
    
    
    /**
     @returns the pretty print mime-type for the passed mime-type or the passed mimetype if no mapping was found.
     */
    public String getPrettyPrintMIMEType( String mimetype ) 
    {
        if ( mimetype == null )
            return null;
        String ret = printMappings.get( mimetype );
        if (ret == null)
            return mimetype;
        else 
            return ret;
    }
    

    /**
        @returns a (array)list of all extensions mapping the passed mime-type.
    */
    public ArrayList<String> getExtensions( String _mimetype ) 
    {
        ArrayList<String> ret = new ArrayList<String>();
        if ( _mimetype == null )
            return ret;
        Enumeration<String> keys = mappings.keys();
        while ( keys.hasMoreElements() == true) {
            String extension = keys.nextElement();
            if ( _mimetype.equalsIgnoreCase( mappings.get(extension) ) )
                ret.add(extension);
            else ;    
        }    
        
        return ret;
    }


    /**
        Gets an array of all configured MIME types.
        Every MIME type is contained only once in this array.
        The array is ordered alphabetically.
        @return a sorted array of all known MIME types
    */
    public String[] getMIMETypeArray()
    {
        ArrayList<String> ret = getMIMETypeList();
        String[] arr = ret.toArray( new String[0] );
        Arrays.sort( arr );
        return arr;
        
    }

    /**
        Gets an unordered list of all configured MIME types.
        Every MIME type is contained only once in this list.
        @return an unsorted array of all known MIME types
    */
    public ArrayList<String> getMIMETypeList()
    {
        ArrayList<String> ret = new ArrayList<String>();        
        for ( Enumeration<String> e = mappings.elements() ; e.hasMoreElements() ;) {
            String mt = e.nextElement();
            if ( ! ret.contains( mt ) )
                ret.add( mt );
            }
        return ret;
        
    }

    /**
        for testing purposes only
    */    
    public static void main(String[] args)
    {
        
        try {
            FileTypeResolver ftr = FileTypeResolver.instantiate();
            System.out.println("FileTypeResolver::main() - start");
            System.out.println(ftr.getMIMEType(new File("hallo.doc")));
            System.out.println(ftr.getExtensions("audio/midi"));
            System.out.println("FileTypeResolver::main() - end");            
        } catch (Exception e) { System.out.println("Exception caught");}
           
    }    


}
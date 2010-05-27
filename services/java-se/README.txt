Services Available using only the Java Standard Edition platform
================================================================


Notes
=====

Currently, J5SE.

The functionality can be extended to cover more formats and functions by 
installing the Java Advanced Imaging ImageIO extension pack.

  https://jai-imageio.dev.java.net

I've added the pure-java jai_imageio jar to this package, and even without 
the DLLs it seems to work find for our needs, but only in 'local' and 'standalone'
modes. For reasons I do not understand, it does not work on the server, even 
though the jar is right in WEB-INF/lib as it should be.


Test Files
==========

Almost all of the test files were generated from PlanetsLogo.png using Paint.NET. 
The exception is the JP2 version, which was actually created using the JAI tools, 
and so is not really an independent test.

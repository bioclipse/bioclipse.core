This fragment project exists to supply a configuration file to the logging
bundle. The bundle host of this fragment is able to look in this bundle for
its default configuration file.

In the case of Log4j, this file is either a properties file called
"log4j.properties" or an XML file called "log4j.xml". (Log4j knows what to do 
with whichever type of file it finds.)

Feel free to edit the configuration file to suit your needs. There is a
"templates" folder in this directory to which you can save your favorite
logging configurations. If you want to share a configuration you have
modified/written, please save it to the templates folder and commit that.

At the time of writing, the default configuration file writes logging output
to the console and also saves it to a file in your home directory named
"bioclipse.log". If you want to change this directory, either hardcode it or
use a system property. (Log4j substitutes into property values using the
obvious ${system.property.name} syntax.) Unfortunately, you cannot use some useful
predefined properties like ${osgi.instance.location}, because their values are
file:/ urls. Log4j does not interpret such urls properly, but absolute
pathnames can be used.

--Richard Klancer 
  rpk@pobox.com 
  March 19, 2008

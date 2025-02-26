package org.example
import java.io.*
import kotlin.system.exitProcess
/*
fun main() {

    //val testURLDatabaseSystem : TestURLDatabaseSystem = TestURLDatabaseSystem()            // Create an object for the test class

    //testURLDatabaseSystem.testUserInput()                                                  // User Input Method

    //testURLDatabaseSystem.testSystemCases()                                                //Test Cases In Array Format
    //testURLDatabaseSystem.testFileTestCases()

}
*/
class URLDatabaseSystem{

    private val database : HashMap<String,List<String>> = HashMap()

    init {
        createDatabase()
    }

    private fun formatURL(url : String) : String{
        var finalString : String = url
        while(finalString[finalString.length - 1] == '/')
            finalString = finalString.substring(0, finalString.length - 1)                    // Since upb.de/site and upb.de/site/ is the same using this method to remove trailing slashes
        return finalString.lowercase()                                                        // to format the url to lower case, helps with duplicates
    }

    private fun createDatabase(): HashMap<String, List<String>>{
        return HashMap()
    }
/*
    private fun checkTechnologies(technologies : String) : Boolean{
        return technologies.trim().length > 0
    }

    private fun checkURL(url : String) : Boolean{
        return url.contains("https://upb.de") && !url.contains(" ")
    }
 */
    private fun getParentUrl(url: String): String{

        if (url.equals("https://upb.de"))                                                           // if the input URL is https://upb.de, it can't have a parent so bypassing it.
            return ""

        val pathInURL : List <String> = url.split("/")                                    // Splitting along the delimitter / as that decided the next child
        val startIndexOfCurrentDirectory = url.length - pathInURL.get(pathInURL.size - 1).length
        val parentUrl : String = formatURL(url.substring(0,startIndexOfCurrentDirectory))           // Removing the last child member to give the parent and remove any trailing /

        //println("Parent Url - $parentUrl")
        return parentUrl
    }

    private fun addCurrentUrlToDatabase(directory: Directory){
        if (database.containsKey(directory.url))
            print("Directory is already in the database. Technologies will be replaces by new values")

        val listOfTechnologies = mutableListOf<String>()

        for(technology in directory.technologies.split(",")){                                                                     // Splitting the input string and storing as a list of strings as this will prevent
            if(technology.trim().length > 0)                                                                                                // Multiple use of the split function and help us format the technologies for bad data
                listOfTechnologies.add(technology.trim().lowercase())
        }
        listOfTechnologies.sort()
        database.put(directory.url,listOfTechnologies)                                                                                      // Add new values or replace an existing value.
    }

    private fun getDifferentTechnologies(parentUrl : String, directory: Directory) : String{

        val  parentTechnologies : List<String>  = if (database.containsKey(parentUrl)) database.get(parentUrl)!! else emptyList<String>()   // Getting technologies for parent URL and input Url from database
        var outputString = ""
        for (technology in directory.technologies.split(",")){
            val formattedTechnology : String = technology.trim().lowercase()
            if (formattedTechnology.length > 0 && checkIfTechnologyNotInParentTechnologies(formattedTechnology, parentTechnologies))
                outputString = outputString.plus(technology).plus(",")                                                                // Creating a concat string using technologies not in parent URL.
        }
        return if (outputString.length > 0) outputString.substring(0,outputString.length -1).trim() else outputString
    }

    private fun checkIfTechnologyNotInParentTechnologies(technology : String, parentTechnologies : List<String>): Boolean{                 // Check each technology in child directory with parent directory using a simple loop

        return parentTechnologies.binarySearch(technology) < 0

    }

    fun simplifyInputTechnologies(directory : Directory) : String {                                  // Method that is the entry point to the class and will perform the processing.

        val formattedUrl = formatURL(directory.url)                                                  // Formating the input url to streamline the data and help identify duplicates
        val parentUrl : String = getParentUrl(formattedUrl)                                          // Generating the parent URL from the input URL to use in our database
        val differentTechnologies = getDifferentTechnologies(parentUrl,directory)                    // Getting a list of different technologies present only in input URl and not parent.
        directory.url = formattedUrl
        addCurrentUrlToDatabase(directory)                                                           // Adding the current url and associated technology to the database
        return differentTechnologies
    }

    fun processCSVFile(inputFilePath : String, outputFilePath : String) {                            // Provide the file paths for input and output file and then the input file is read and processed

        try {
            val inputStream : InputStream = File(inputFilePath).inputStream()                        //Create an input stream for the Input File
            val reader : BufferedReader = inputStream.bufferedReader()
            val file : File = File(outputFilePath)                                                   // Create an output file if does not exist else modify the existing one.
            val outputStream : OutputStream = FileOutputStream(file)                                 //Create an output stream for the file.
            val writer = BufferedWriter(OutputStreamWriter(outputStream))

            try{
                var line : String = reader.readLine()                                                // Read data from input file line by line until EOF not reached.
                var index : Int = 1;
                while (line != null) {
                    var outputLine = ""
                    val directory : Directory = processCSVFileInput(line)                            // Used to split the data in the line and obtain the URL and the List of technologies from it.

                    if (line.isEmpty())
                        outputLine = "No Input"
                    else if (!directory.url.equals("Incorrect Input Format"))
                        outputLine = line + ",\""+simplifyInputTechnologies(directory)+"\""          // Call the method that compares the list of technologies and simplifies it.
                    else if (index != 1)
                        outputLine = "Error"
                    else
                        outputLine = line + "," + "Output Technologies"                              // Based on the input and output create a string that will be written to the output file

                    writeToCSVFile(writer,outputLine)
                    line = reader.readLine()
                    index++
                }
            }
            catch(e : IOException){
                println("Error while processing the CSV file")                                       //reader.readline() can throw IO exception so to catch the same and throw the exception again because of the nested try block
                //e.printStackTrace()
                throw IOException()
            }
            finally {
                writer.close()                                                                       // Close reader and writer in case of exception or normally too.
                reader.close()
            }
        }
        catch(f : FileNotFoundException){                                                            // If input file is not found.
            //f.printStackTrace()
            println("File Not Found")
            //exitProcess(1)
        }
        catch (e : IOException){
            //exitProcess(1)
        }
        catch (e : Exception){                                                                       // Catch any other general exception that may araise.
            println("Error while processing the Data")
            //exitProcess(1)
        }

    }

    private fun processCSVFileInput(line : String) : Directory {                                     //Processing each line and spliting along delimiter to seperate URL and Technology

        if (line.isEmpty() || line.split(",\"").size !=2)
            return Directory("Incorrect Input Format","")

        val lineComponents : List<String> = line.split(",\"")
        val url = lineComponents[0]
        val technologies : String = lineComponents[1].substring(0,lineComponents[1].length - 1)

        return Directory(url,technologies)
    }

    private fun writeToCSVFile(writer: BufferedWriter, outputString : String) {                       //Using to write to the file created/opened. Flush is used to make sure all the data in buffer is written for each line.

        writer.write(outputString)
        writer.newLine()
        writer.flush()

    }


    class Directory(var url : String, var technologies: String){
    }

}
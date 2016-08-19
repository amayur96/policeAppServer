Backend Server for Mobile App
Written by Gene Li, Arjun Mayur, and Ayan Mukhopadhyay

For any questions, please contact Gene at gxli@princeton.edu.

8/12/16
—————————————————
Description:
This is a rudimentary backend for the Android app that was developed in summer of ’16. It uses a Java Servlet and Apache Tomcat to host the servlet. Databases are written with SQLite. 

The backend implements a RESTful web service that is locally hosted. It services GET and POST requests from the front end. GET request, along with supplied precinct and car ID key-value pairs, returns a JSON file that contains information such as the location of police cars, a heatmap, crimes, and the next location which to travel to. POST request is used to attach a JSON file containing datetime, car ID, lat, and long, which is then stored in the database table “police”. 

For route generation, we are using a simple greedy heuristic for the transportation problem. At each iteration, the smallest distance between police and crime is selected, with the police being assigned to that crime, until all crimes are assigned a police. However, this does not produce the optimal solution, and it is clear to see how in some cases this might result in a far from optimal solution. Another option is using CPLEX to solve the transportation problem, but CPLEX is $$$.

The data is stored in /Databases/test.db. There are three tables crimeTest, heatmap, and police which served as a proof of concept at the demo. crimeTest contains a list of crimes which are generated from the actual data source. It has 12 on call crimes, 6 of which are in the north precinct and 6 in the south precinct. police contains 12 patrol cars, 6 each in north and south precinct. heatmap contains heatmap data, with lat long and weight that is used to populate the heatmap of the client app. All data in the test database is dummy data.

—————————————————
How to install:
To install, please clone from the repository. On your computer, you should have a folder now named policeAppServer with all of the files inside.
You’ll also need Eclipse for EE developers.

Then open up Eclipse for EE. Set the workspace directory to the PARENT directory of your clone. So if your clone is /Documents/policeAppServer, set your working directory to /Documents.

Then you’ll have to import the project. To import, go to File->Import and import an Existing Projects into Workspace. Select your root directory as the same as the workspace. Then you’ll see a list of projects, import the policeserver project and the Servers project.

At this point you’ll have two projects and a few errors. To add Apache Tomcat 7, you’ll need to go to Eclipse preferences and find Server Runtime Environments. Add Apache Tomcat v7.0, and point the installation directory to the folder inside of your cloned repo. Finish.

Now right click on policeserver in your project explorer, click on java-json.jar and edit it. Point it to the right location which should be inside your cloned repo. Same thing with the mysql connector.

To view the database, download SQLiteStudio.

If I forgot any steps and it didn’t work, just email me.

———————————————
How to run:

Inside of src, you’ll have dbConnection and policePatrolServlets. Don’t worry about dbConnection. policePatrolServlets: the main code to run is updateJSON.java. There are also three test files that tested out various features. updateJSON.java should be run on the server. When you do so, you’ll have a server up and running. you can open up your browser and type something like:

http://localhost:8080/policeserver/updateJSON

Or to specify precinct,

http://localhost:8080/policeserver/updateJSON?precinct=North

Or precinct and ID:

http://localhost:8080/policeserver/updateJSON?precinct=North&ID=32A

These should all return JSON files. Note that if you try to connect from the app emulator, you’ll need to replace localhost with 10.0.2.2
———————————————————
Future Directions:

This is a very barebones application that does the minimum, just to give you an idea of the requirements for functionality. In the future, this will have to be entirely rewritten in collaboration with the police department using a backend framework. It will have to incorporate real data and encryption.




——————————
Forecast model is in ClusteringAlgorithm.java It’s not complete.

# CSE360 Wednesday 44 Team Project: Phase 2

Overview
This Java application provides a platform for users to post questions and answers, similar to a discussion forum. The application allows for CRUD operations (Create, Read, Update, Delete) on questions and answers, as well as searching and marking questions and answers as resolved.

Classes Description
DatabaseHelper:
Handles database connections and operations.
Manages user registration, question and answer manipulation.
Ensures questions and answers can be added, updated, deleted, and searched through.
This phase of the CSE360 project implements functionality from the "Student" user stories.

## Installation
1. Dependencies:
   - Java JDK 23.0.1 Library
   - JavaFX JDK 23.0.1 Library
   - H2 Library
2. Download TeamProjPhase2.zip to your machine.
3. Unzip and import into your desired workspace; this project was built in Eclipse, therefore the following instructions are for Eclipse.
   - Ensure the Modulepath contains both JavaFX and the jdk-23 system library, and ensure the classpath contains H2.

Question and Questions:
Question: Represents a single question with properties like text, author, and resolution status.
Questions: Manages a list of questions, supporting operations like add, delete, and search.

Answer and Answers:
Answer: Represents a single answer linked to a question, including the text and author.
Answers: Manages a list of answers, providing functionalities to add, update, and remove answers.

DiscussionPage:
Interfaces with DatabaseHelper to display and manage the user interface for questions and answers.
Allows users to post new questions and answers, edit existing ones, and mark them as resolved.

StudentHomePage:
Displays a simple welcome page for students.
Provides links to log out or navigate to the discussion forum.

TestAutomation:
Automated tests for validating the functionality of CRUD operations, search features, and resolution status updates.

Features
CRUD Operations: Create, read, update, and delete questions and answers.
Search Functionality: Search for questions or answers based on keywords.
Resolution Flag: Mark questions or answers as resolved to indicate closure.

Running the Application:
To start the application, launch it from the StartCSE360 class. Follow the prompts to set up a new student user account. Once registered and logged in, you can navigate to the Discussion Forum to participate in posting questions and answers.

***Note: Please download the TeamProjPhase2.zip and run into your machine!
Stand Up Meetings:

https://drive.google.com/file/d/1H18rJOzNveG4V3t9Ei2Tkh7Zd6O6oGPk/view?usp=sharing

https://drive.google.com/file/d/1BHZ54fPE-_x_hVpXmNdmM_LmVMqg7lkK/view?usp=sharing 

Program Screencasts:

https://asu.zoom.us/rec/share/LrToXmiyHEBS06UJhVUn9Y518qx92DJmN0n8JhNpJslQR9yhh768kTV-mF8fd31o.tqV4feKhDoxQIhtw?startTime=1740630604000

https://asu.zoom.us/rec/share/LrToXmiyHEBS06UJhVUn9Y518qx92DJmN0n8JhNpJslQR9yhh768kTV-mF8fd31o.tqV4feKhDoxQIhtw?startTime=1740630604000

https://asu.zoom.us/rec/share/GsRZ8VJCzIM4XD9Sto1me7jn4RoD3Oop7w29IMlDik2sTxiREwS9EjkgLehhtMcb.mU4Vd7CHj7wDm5sf?startTime=1740632198000

https://www.youtube.com/watch?v=VzJutLqiGHM   

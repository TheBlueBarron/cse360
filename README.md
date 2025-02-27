# CSE360 Wednesday 44 Team Project: Phase 2

Overview
This Java application provides a platform for users to post questions and answers, similar to a discussion forum. The application allows for CRUD operations (Create, Read, Update, Delete) on questions and answers, as well as searching and marking questions and answers as resolved.

Classes Description
DatabaseHelper:
Handles database connections and operations.
Manages user registration, question and answer manipulation.
Ensures questions and answers can be added, updated, deleted, and searched through.

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

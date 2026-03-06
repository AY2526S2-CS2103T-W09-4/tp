---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* {list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in this document `docs/diagrams` folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* Has a need to manage a significant number of team members and their task assignments (mainly project managers)
* Prefer desktop apps over other types
* Can type fast
* Prefers keyboard-driven workflows over traditional GUI-heavy tools
* Example: Tech lead and project managers who prefer efficient keyboard-driven workflows, comfortable working with CLI commands and need to manage multiple projects and team members regularly.

**Value proposition**: manage tasks and projects more efficiently with keyboard-based interaction rather than GUI. This project management system supports dynamic task tracking and human resource management.

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​ | I want to …​                           | So that I can…​                                                              |
|----------|------|----------------------------------------|------------------------------------------------------------------------------|
| `* * *`  | user | add a contact                          | keep track of project members.                                               |
| `* * *`  | user | delete a contact                       | remove outdated information or remove a member from the project.             |
| `* * *`  | user | add a project to a contact             | assign member to the project                                                 |
| `* * *`  | user | delete a project from a contact        | remove members from a project                                                |
| `* * *`  | user | assign tasks to contact                | clearly know about their responsibilities                                    |
| `* * *`  | user | delete tasks from a contact            | easily remove tasks that is falsely assigned to the contact or has been done |
| `* * *`  | user | view all contacts                      | see all the project members contacts                                         |
| `* * *`  | user | view all projects                      | easily have an overview of all projects                                      |
| `* * *`  | user | view all tasks assigned to the contact | see all the tasks assigned to a contact                                      |

*{More to be added}*

### Use cases

(For all use cases below, the **System** is the `TaskForge` and the **Actor** is the `user`, unless specified otherwise)

**Use case: UC01 Add a contact**

**Guarantees**

1. The new contact specified will be added to TaskForge if the command parameters are valid.
2. The new contact will not be added to TaskForge if there is a missing or invalid parameter needed for the contact.

**MSS**

1.  User enters the command in the input to add a new contact.
2.  TaskForge displays a success message in the reply dialog.
3.  TaskForge displays the new contact in the contact list.

    Use case ends.

**Extensions**

* 1a. TaskForge detects incorrect command input.

    * 1a1. TaskForge displays an error message on the invalid parameter.
    * 1a2. User enters the command again.
    * Steps 1a1-1a2 are repeated until the command entered is valid.

      Use case resumes at step 2.
  Use case ends.

**Use case: UC02 Add a project to a contact**

**Guarantees**

1. The new project will be successfully added to the specified contact when the command is valid.
2. The new project will not be added to non-existing or invalid contact.

**MSS**

1. User requests to view all contacts.
2. TaskForge displays the list of contacts.
3. User requests to add a project to a contact.
4. TaskForge checks the existence of the contact.
5. TaskForge adds a project to a contact and displays success message.

   Use case ends.

**Extensions**

* 1a. TaskForge detects invalid command input.
	 * 1a1. TaskForge displays an error message.
	 * 1a2. User enters the command again.
    * Steps 1a1-1a2 are repeated until the input entered are valid.
   
   Use case resumes from step 2.

* 2a. TaskForge could not find the mentioned contact.
	 * 2a1. TaskForge displays an error message.
	 * 2a2. User enters the command again.
	 * Steps 2a1-2a2 are repeated until the input entered are valid.
   
   Use case resumes from step 2.

**Use case: UC03 Delete a task from a contact**

**Guarantees**

1. The task is deleted from the contact if successful.
2. The task is not deleted if there is missing information or invalid input.

**MSS**

1. User requests for a list of all tasks assigned to the contact.
2. TaskForge displays the tasks assigned to the selected contact.
3. User requests to delete the selected task.
4. TaskForge deletes the task and shows a confirmation message.

   Use case ends.

**Extensions**

* 1a. Contact does not exist.
	 * 1a1. TaskForge shows an error message.
	 * 1a2. User enters new input.
    * Steps 1a1-1a2 are repeated until the input entered are valid.
   
   Use case ends.

* 2a. Task does not exist.
	 * 2a1. TaskForge shows an error message.
	 * 2a2. User enters new input.
	 * Steps 2a1-2a2 are repeated until the input entered are valid.
   
   Use case ends.

**Use case: UC04 View all contacts**

**MSS**

1. User request to view all contacts.
2. TaskForge return the list with all contacts.

   Use case ends.

**Extensions**

* 1a. TaskForge detects error in CLI command.
	 * 1a1. TaskForge returns the error message.
	 * 1a2. User input another command.
    * Steps 1a1 - 1a2 are repeated until the command is correct.
   
   Use case ends.

*{More to be added}*

### Non-Functional Requirements

#### Usability
- A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
- A user should be able to use the app without mouse / button interactions.
- The GUI should display correctly without resolution-related issues on screen resolutions 1920×1080 and higher with 100% and 125% screen scaling. The system should remain usable (all functions accessible even if the layout is not optimal) on resolutions 1280×720 and higher with 150% screen scaling.
- The product should be for a single user.

#### Technical
- The product should work on any mainstream OS as long as it has Java 17.
- The product should not depend on external remote servers.

#### Constraints
- The product needs to be developed in a breadth-first incremental manner over the project duration.

#### Data
- All data is stored locally in a human editable file.
- The product should not use a Database management system  to store data.

#### Process
- Every PR need to pass the CI and page deployment checks before merging.
- The product should be packaged into 1 JAR file only.

#### Portability
- The app should run without requiring any installation (excepting for Java 17 installation).

#### Performance
- The system should respond to user commands within 5 seconds under normal usage conditions.
- Starting the application requires less than 5 seconds.

#### Capacity
- The system should be able to handle at least 100 contacts and 1000 tasks without noticeable performance degradation.
- The product JAR/ZIP file should be at maximum 100MB and each document should be at maximum 15MB.

#### Security
- The system should store user data locally and should not transmit information over a network.

#### Robustness
- The system should handle invalid user inputs gracefully, displaying appropriate error messages instead of crashing.

#### Testability
- The system should support automated unit tests and integration tests to verify system functionality.

#### Reliability
- The system should not lose stored data during normal operation.
- The system should not corrupt data on unexpected shutdown.

#### Maintainability
- The code should follow good Object-Oriented Programming principles.
- The code should maintain a good approach in coding standard.
- The developer guide and user guide should be PDF-friendly.

### Glossary

* **Contact**: A person who is involved in a project and can be assigned tasks
* **Task**: A unit of project that needs to be completed by a contact
* **Project**: A collection of tasks and contacts organized to achieve a specific goal
* **GUI**: Graphical user interface of the application
* **Mainstream OS**: Mainstream OS stands for Mainstream Operating System which include MacOS, Linux and Windows
* **PR**: Pull request in Github
* **CLI**: A command line interface (CLI) is a text-based interface where you can input commands that interact with a computer's operating system.
* **User**: Project managers and team leaders who preferred CLI
* **JAR**: Java ARchive, a file format to run java program
* **ZIP**: a file format to compress one or more files into 1 single file
* **CI**: Continuous Integration which is a software development practice in which code changes are frequently integrated into a shared repository and automatically built and tested to detect issues early
* **Page deployment**: The product website page that is being deployed on Github
* **Unit test**: A testing method to test units of code
* **Integration test**: A software testing phase where individual units or modules are combined and tested as a group to verify they function correctly together
* **Command**: A text instruction entered by the user in the CLI to perform a specific operation in the system
* **Command parameters**: Parameters that are part of a command to specify information
* **Remote servers**: A computer located elsewhere that stores data, hosts applications, or processes tasks, accessible over a network or the Internet
* **Breadth-first Incremental manner**: A development strategy where small portions of functionality are added across the whole system step by step, instead of completing one feature fully before starting another

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_

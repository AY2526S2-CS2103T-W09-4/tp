package seedu.taskforge.logic.commands.task;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.taskforge.commons.core.index.Index;
import seedu.taskforge.logic.commands.CommandResult;
import seedu.taskforge.logic.commands.exceptions.CommandException;
import seedu.taskforge.model.Model;
import seedu.taskforge.model.project.Project;
import seedu.taskforge.model.task.Task;
import seedu.taskforge.model.task.exceptions.DuplicateTaskException;

/**
 * Edits an existing task name from an existing project in the address book.
 */
public class EditTaskCommand extends TaskCommand {
    public static final String SUBCOMMAND_WORD = "edit";

    public static final String MESSAGE_SUCCESS = "Task edited in project: %1$s";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " "
            + SUBCOMMAND_WORD + " PROJECT_NAME -i TASK_INDEX_FROM_PROJECT -n NEW_TASK_NAME";
    public static final String MESSAGE_PROJECT_NOT_FOUND = "The project was not found in the address book.";
    public static final String MESSAGE_INDEX_OUT_OF_BOUND = "Task index is out of bound";
    public static final String MESSAGE_DUPLICATE_TASK = "This task already exists for this project!";

    private final Project targetProject;
    private final Index taskIndex;
    private final Task newTask;

    /**
     * Creates an EditTaskCommand to edit a task in a project.
     */
    public EditTaskCommand(Project targetProject, Index taskIndex, Task newTask) {
        requireNonNull(targetProject);
        requireNonNull(taskIndex);
        requireNonNull(newTask);

        this.targetProject = targetProject;
        this.taskIndex = taskIndex;
        this.newTask = newTask;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Project projectToEdit = model.getProjectList().stream()
                .filter(project -> project.equals(targetProject))
                .findFirst()
                .orElseThrow(() -> new CommandException(MESSAGE_PROJECT_NOT_FOUND));

        Task taskToEdit;
        try {
            taskToEdit = projectToEdit.getTasks().get(taskIndex.getZeroBased());
        } catch (IndexOutOfBoundsException e) {
            throw new CommandException(MESSAGE_INDEX_OUT_OF_BOUND);
        }

        Task renamedTaskWithProject = createTaskWithProjectAndStatus(newTask.description,
                projectToEdit.title, taskToEdit);
        Project editedProject = createEditedProject(projectToEdit, taskToEdit, renamedTaskWithProject);

        model.setProject(projectToEdit, editedProject);

        return new CommandResult(String.format(MESSAGE_SUCCESS, editedProject));
    }

    private static Project createEditedProject(Project projectToEdit, Task taskToEdit, Task renamedTask)
            throws CommandException {
        Project editedProject = new Project(projectToEdit.title, projectToEdit.getTasks());
        try {
            editedProject.getUniqueTaskList().setTask(taskToEdit, renamedTask);
        } catch (DuplicateTaskException e) {
            throw new CommandException(MESSAGE_DUPLICATE_TASK);
        }
        return editedProject;
    }

    private static Task createTaskWithProjectAndStatus(String description, String projectTitle, Task sourceTask) {
        Task task = new Task(description, projectTitle);
        if (sourceTask.getStatus()) {
            task.setDone();
        }
        return task;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof EditTaskCommand)) {
            return false;
        }

        EditTaskCommand otherEditTaskCommand = (EditTaskCommand) other;
        return targetProject.equals(otherEditTaskCommand.targetProject)
                && taskIndex.equals(otherEditTaskCommand.taskIndex)
                && newTask.equals(otherEditTaskCommand.newTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetProject, taskIndex, newTask);
    }
}


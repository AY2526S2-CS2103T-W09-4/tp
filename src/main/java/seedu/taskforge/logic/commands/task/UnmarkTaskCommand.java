package seedu.taskforge.logic.commands.task;

import static java.util.Objects.requireNonNull;
import static seedu.taskforge.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.taskforge.commons.core.index.Index;
import seedu.taskforge.logic.Messages;
import seedu.taskforge.logic.commands.CommandResult;
import seedu.taskforge.logic.commands.exceptions.CommandException;
import seedu.taskforge.model.Model;
import seedu.taskforge.model.person.Person;
import seedu.taskforge.model.person.PersonTask;
import seedu.taskforge.model.project.Project;
import seedu.taskforge.model.task.Task;

/**
 * Unmarks a task as done for an existing person in the address book.
 */
public class UnmarkTaskCommand extends TaskCommand {
    public static final String SUBCOMMAND_WORD = "unmark";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + SUBCOMMAND_WORD
            + ": Unmarks the task identified by the task index as done for the person identified by the person index.\n"
            + "Parameters: PERSON_INDEX TASK_INDEX\n"
            + "Example: " + COMMAND_WORD + " " + SUBCOMMAND_WORD + " 1 1";

    public static final String MESSAGE_UNMARK_TASK_SUCCESS = "Marked task as not done: %1$s";
    public static final String MESSAGE_TASK_ALREADY_NOT_DONE = "This task is already marked as not done.";
    public static final String MESSAGE_INVALID_TASK_REFERENCE = "Task reference is invalid.";

    private final Index personIndex;
    private final Index taskIndex;

    /**
     * @param personIndex of the person in the filtered person list
     * @param taskIndex of the task in the person's task list
     */
    public UnmarkTaskCommand(Index personIndex, Index taskIndex) {
        requireNonNull(personIndex);
        requireNonNull(taskIndex);

        this.personIndex = personIndex;
        this.taskIndex = taskIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (personIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(personIndex.getZeroBased());
        List<PersonTask> taskList = personToEdit.getTasks();

        if (taskIndex.getZeroBased() >= taskList.size()) {
            throw new CommandException(DeleteTaskCommand.MESSAGE_INDEX_OUT_OF_BOUND);
        }

        PersonTask taskToUnmark = taskList.get(taskIndex.getZeroBased());
        Task resolvedTask = resolveTask(model, taskToUnmark);
        if (!resolvedTask.getStatus()) {
            throw new CommandException(MESSAGE_TASK_ALREADY_NOT_DONE);
        }

        Project project = model.getProjectList().get(taskToUnmark.getProjectIndex());
        List<Task> updatedTasks = project.getTasks();
        updatedTasks.get(taskToUnmark.getTaskIndex()).setNotDone();
        Project editedProject = new Project(project.title, updatedTasks);

        model.setProject(project, editedProject);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_UNMARK_TASK_SUCCESS, resolvedTask.description));
    }

    private static Task resolveTask(Model model, PersonTask personTask) throws CommandException {
        int projectIndex = personTask.getProjectIndex();
        int taskIndex = personTask.getTaskIndex();
        if (projectIndex < 0 || projectIndex >= model.getProjectList().size()) {
            throw new CommandException(MESSAGE_INVALID_TASK_REFERENCE);
        }
        List<Task> projectTasks = model.getProjectList().get(projectIndex).getTasks();
        if (taskIndex < 0 || taskIndex >= projectTasks.size()) {
            throw new CommandException(MESSAGE_INVALID_TASK_REFERENCE);
        }
        return projectTasks.get(taskIndex);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UnmarkTaskCommand)) {
            return false;
        }

        UnmarkTaskCommand otherUnmarkTaskCommand = (UnmarkTaskCommand) other;
        return personIndex.equals(otherUnmarkTaskCommand.personIndex)
                && taskIndex.equals(otherUnmarkTaskCommand.taskIndex);
    }
}

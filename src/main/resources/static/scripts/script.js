let titleInputBox = $('#titleNoteInput');
let textNoteInputBox = $('#noteTextInput');

function auto_grow(element) {
    element.style.height = "auto";
    element.style.height = (element.scrollHeight) + "px";
}


// Add task to the form when the user clicks the add button
$('#addTaskButton').click(function () {
    let taskName = $('#newTaskInput').val();
    if (taskName) {
        let taskId = 'task' + ($('#tasksForm input[type="checkbox"]').length + 1);
        $('#tasksForm > .modal-body').append('<div class="form-check"><input class="form-check-input" type="checkbox" value="' + taskName + '" id="' + taskId + '" name="tasks[]"><label class="form-check-label" for="' + taskId + '">' + taskName + '</label></div>');
        $('#newTaskInput').val('');
    }

});

// Save selected tasks when the user clicks the save button
$('#saveTasksButton').click(function () {
    let selectedTasks = $('#tasksForm input[type="checkbox"]:checked').map(function () {
        return $(this).val();
    }).get();
    console.log(selectedTasks);
    // do something with the selected tasks
    // ...
});


$('#saveNoteButton').click(() => {
    let podatok = {};

    podatok['title'] = titleInputBox.val();
    podatok['text'] = textNoteInputBox.val();


    console.log(titleInputBox.val());
    $.ajax({
        url: 'giveNote',
        type: 'POST',
        dataType: 'json',
        data: JSON.stringify(podatok),
        success: (dataP) => {
            console.log(dataP)
        },
        error: (jqXhr) => {
            console.log(jqXhr);
        }

    })
})


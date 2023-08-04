const titleInputBox = $('#titleNoteInput');
const textNoteInputBox = $('#noteTextInput');
const taskForm = document.getElementById("tasksForm");
const taskTitle = document.getElementById("TaskTitle");
const notesContainer = document.getElementById("notesContainer");
const savingStatus = document.getElementById("status-spinner");
const addImageModal = document.getElementById('addImageModal')


function auto_grow(element) {
    element.style.height = "auto";
    element.style.height = (element.scrollHeight) + "px";
}


var $grid = $('#notesContainer').packery({
    itemSelector: '.col',
    // columnWidth helps with drop positioning
    columnWidth: 80,
    gutter: 3
});

$grid.imagesLoaded().progress( function() {
    $grid.packery();
});
$grid.find('.col').each(function (i, gridItem) {
    var draggie = new Draggabilly(gridItem);
    // bind drag events to Packery
    $grid.packery('bindDraggabillyEvents', draggie);
});


function orderItems() {
    console.log($grid.packery('getItemElements'));

}

$grid.on('layoutComplete', orderItems);

document.querySelectorAll(".add-color-button").forEach(colorButton => {
    let colorSelector = new bootstrap.Popover(colorButton, {
        container: 'body',
        placement: 'bottom',
        fallback: 'bottom',
        html: true,
        trigger: 'click',
        customClass: 'color-popover',
        content: document.getElementById('colorpopover-content'),
    })
    let popovercont = document.getElementById('colorpopover-content')
    popovercont.addEventListener("mouseleave",()=>{
        colorSelector.hide();
    })
    document.addEventListener("click",()=>{
        colorSelector.hide();
    })
    colorButton.addEventListener('shown.bs.popover', () => {
        colorButton.classList.add('focused-color-button');
    })
    colorButton.addEventListener('hidden.bs.popover', () => {
        colorButton.classList.remove('focused-color-button');
    })
})




// Add task to the form when the user clicks the add button
$('#addTaskButton').click(function () {
    let taskName = $('#newTaskInput').val();
    if (taskName) {
        let taskId = 'task' + ($('#tasksForm input[type="checkbox"]').length + 1);
        $('#tasksForm > .modal-body').append('<div class="form-check mt-2"><input class="form-check-input" type="checkbox" value="' + taskName + '" id="' + taskId + '" name="tasks[]"><label class="form-check-label" for="' + taskId + '">' + taskName + '</label></div>');
        $('#newTaskInput').val('');
    }

});

// Save selected tasks when the user clicks the save button
$('#saveTasksButton').click(function () {
    // let allTasks = $('#tasksForm input[type="checkbox"]').map(function () {
    //     return $(this).val();
    // }).get();
    savingStatus.hidden = false;

    let allTasks = {}
    $('#tasksForm input[type="checkbox"]').each((i, x) => {

        allTasks[$(x).val()] = (($(x).prop('checked')));
    });


    console.log(allTasks);

    // let fd = new FormData(taskForm);

    let data = {};
    // data['form'] = fd.entries();
    data['title'] = taskTitle.value;
    data['allTasks'] = allTasks;


    $.ajax({
        url: 'giveTask', type: 'POST', dataType: 'json', data: JSON.stringify(data),

        success: (dataP) => {
            console.log(dataP)
            savingStatus.hidden = true;


        }, error: (jqXhr) => {
            console.log(jqXhr);
        }

    })


});

//TODO: dodavanje na taskoj
//TODO: editiranje na taskoj

$('#saveNoteButton').click(() => {

    savingStatus.hidden = false;

    let data = {};

    data['title'] = titleInputBox.val();
    data['text'] = textNoteInputBox.val();


    console.log(titleInputBox.val());

    $('#saveNoteButton').html(`<div class="spinner-border spinner-border-sm" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>`).prop("disabled", true);


    $.ajax({
        url: 'giveNote', type: 'POST', dataType: 'json', data: JSON.stringify(data), success: (dataP) => {
            console.log(dataP)
            let noteData = dataP[0];

            let template = makeNote(noteData['title'], noteData['text'], noteData['id']);
            let element = $($.parseHTML(template));


            toBinButton(element.find('.delete-note-button')[0]);
            console.log(element);
            $grid.prepend( element[0] ).packery('prepended',element[0]);


            $('#saveNoteButton').html('Save changes').prop("disabled", false);

            titleInputBox.val("");
            textNoteInputBox.val("");

            savingStatus.hidden = true;


        }, error: (jqXhr) => {
            console.log(jqXhr);
        }

    })
})
//todo:istoto za task
document.querySelectorAll(".taskCheckmark").forEach(elem => {
    elem.addEventListener("change", () => {
        savingStatus.hidden = false;

        let data = {}

        data['id'] = elem.name;
        data['checked'] = elem.checked;

        if (elem.checked) elem.nextElementSibling.classList.add("strike"); else elem.nextElementSibling.classList.remove("strike");


        $.ajax({
            url: 'changeStatus', type: 'POST', dataType: 'json', data: JSON.stringify(data), success: (dataP) => {
                console.log(dataP)
                savingStatus.hidden = true;

            }, error: (jqXhr) => {
                console.log(jqXhr);
            }

        })

    })
})


document.querySelectorAll(".delete-note-button").forEach(elem => {
    toBinButton(elem);
})


function toBinButton(elem) {
    elem.addEventListener("click", () => {
        savingStatus.hidden = false;

        let data = {};

        data['id'] = elem.value;
        $(elem.parentElement.parentElement.parentElement).fadeOut(200, () => {

            $.ajax({
                url: 'binCard', type: 'POST', dataType: 'json', data: JSON.stringify(data), success: (dataP) => {
                    console.log(dataP);
                    $grid.packery( 'remove', elem )

                        .packery('shiftLayout');
                    $(elem).remove();
                    savingStatus.hidden = true;

                }, error: (jqXhr) => {
                    console.log(jqXhr);
                }

            })
        })

    })
}

function makeNote(title, text, id) {
    let template = `<div class="col ">
        <div class="card appCard position-relative " style="width: 14rem;">

                <div class="card-body">
                    <h5 class="card-title">${title}</h5>
                    <p class="card-text" 
                       >
                    ${text}
                    </p>
                    
                  
                </div>


                <div class="d-flex flex-row ">
                    <button class="flex-grow-1 btn p-3 text-center add-color-button">
                        <i class="fa-solid fa-brush"></i>
                    </button>
                    <button class="flex-grow-1 btn  p-3 text-center add-image-button"
                    data-bs-target="#addImageModal"
                    value="${id}">
                        <i class="fa-solid fa-image"></i>
                    </button>
                    <button
                        class="flex-grow-1 btn btn-danger rounded-0 p-3 text-white text-center delete-note-button"
                        value="${id}">
                        <i class="fa-solid fa-trash"></i>
                    </button>

                </div>

                <div class="position-absolute top-0 end-0 me-1  ">
                    <button class="btn pin mt-0 me-0">
                        <i class="fa-solid fa-map-pin"></i>
                    </button>
                </div>
        </div>
    </div>`;

    return template;

}



if (addImageModal) {
    addImageModal.addEventListener('show.bs.modal', event => {

        const button = event.relatedTarget

        const value = button.getAttribute('value')

        console.log(value);

        const inputId = addImageModal.querySelector('#addImageModalId')

        inputId.value = value;
    })
}

document.querySelectorAll(".color-circle").forEach(colordiv => {
    let originalBase = colordiv.getAttribute("data-color");
    let originalLight = colordiv.getAttribute("data-light");
    let originalDark =  colordiv.getAttribute("data-dark");
    let card = null;

    colordiv.addEventListener("mouseover", () => {
        let base = colordiv.getAttribute("data-color");
        let light = colordiv.getAttribute("data-light");
        let dark = colordiv.getAttribute("data-dark");

        card = document.getElementsByClassName("focused-color-button").item(0).parentElement.parentElement

        originalBase = card.getAttribute("--c");
        originalLight = card.getAttribute("--brighter");
        originalDark = card.getAttribute("--backgroundC");

        card.style.setProperty("--c", base);
        card.style.setProperty("--brighter", light);
        card.style.setProperty("--backgroundC", dark);


    })
    colordiv.addEventListener("mouseout", () => {
        card.style.setProperty("--c", originalBase);
        card.style.setProperty("--brighter", originalLight);
        card.style.setProperty("--backgroundC", originalDark);
        console.log(card);
    })
    colordiv.addEventListener("click", () => {
        savingStatus.hidden = false;
        let id = card.getAttribute("data-id");

        base = colordiv.getAttribute("data-color");
        light = colordiv.getAttribute("data-light");
        dark = colordiv.getAttribute("data-dark");
        originalBase = base;
        originalLight = light;
        originalDark = dark;
        let data = {}

        data['id'] = id;
        data['base'] = base;

        $.ajax({
            url: 'changeColor', type: 'POST', dataType: 'json', data: JSON.stringify(data), success: (dataP) => {


                savingStatus.hidden = true;

            }, error: (jqXhr) => {
                console.log(jqXhr);
            }

        });
    })

});

//TODO: zacuvuvanje na redosled



input.onfocus = function () {
    browsers.style.display = 'block'; // Отображает элемент
    input.style.borderRadius = "5px 5px 0 0";  // Убирает скругление снизу КР
    memberInput.focus();
};

memberInput.onblur = function () {
    browsers.style.display = 'none'; // Прячет выбор
    input.style.borderRadius = "5px"; // Восстанавливает скругление
};

for (let option of browsers.options) {
    option.onclick = function () {
        let elem = document.activeElement;
        window.status = elem.innerHTML;
        option.disabled = true;
        option.hidden = true;
        newChosen(option)
        browsers.style.display = 'none'; // Прячет выбор
        input.style.borderRadius = "5px"; // Восстанавливает скругление
    }
}


input.oninput = function() { // При вводе?
    currentFocus = -1; // Выбранный элемент
    var text = input.value.toUpperCase();
    for (let option of browsers.options) { // Если совпало - показываем
        if(option.text.toUpperCase().indexOf(text) > -1) {
            option.style.display = "block";
        } else {
            option.style.display = "none";
        }
    }
}

function newChosen(value) {
    const elem = create(`
    <div class="choosen" id="${'choosen' + value.value}">
            <div class="choosen-name">${value.text}</div>
            <div class="choosen-manager">
                <h4 class="checkbox-label">Manager</h4>
                <input type="checkbox" class="custom-checkbox" value="${value.value}" name="isManager">
                <input hidden name="member" value="${value.value}">
            </div>
            <div class="choosen-cancel">
                Delete
                <button id="deleteButton" name="delete" onclick="deleteChoosen('${value.value}')">X</button>
            </div>
    </div>
    `);
    chosenList.appendChild(elem)
}


function deleteChoosen(val) {
    document.getElementById('choosen' + val).remove();
    for (let option of browsers.options) {
        if (option.value === val) {
            option.disabled = false;
            option.hidden = false;
        }
    }
}


function create(htmlStr) {
    var frag = document.createDocumentFragment(),
        temp = document.createElement('div');
    temp.innerHTML = htmlStr;
    while (temp.firstChild) {
        frag.appendChild(temp.firstChild);
    }
    return frag;
}
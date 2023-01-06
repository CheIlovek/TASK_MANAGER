const selectBtn = document.querySelector(".select-btn"),
    items = document.querySelectorAll(".item"),
    arr_inputs = document.querySelectorAll(".member-input");

selectBtn.addEventListener("click", () => {
    selectBtn.classList.toggle("open");
});

items.forEach((item,key) => {
    item.addEventListener("click", () => {
        item.classList.toggle("checked");

        let checked = document.querySelectorAll(".checked"),
            btnText = document.querySelector(".btn-text");
        arr_inputs[key].disabled = !checked;

        if(checked && checked.length > 0){
            btnText.innerText = `${checked.length} Selected`;
        }else{
            btnText.innerText = "Select Language";
        }
    });
})

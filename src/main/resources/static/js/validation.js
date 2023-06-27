function validateForm() {
    var email = document.getElementById("inputEmail").value;
    var login = document.getElementById("inputLogin").value;
    var pw1 = document.getElementById("inputPassword").value;
    var pw2 = document.getElementById("inputPasswordAgain").value;

    if (email == "" || login == "" || pw1 == "" || pw2 == "") {
        alert("Please fill out all fields");
    } else if (pw1 != pw2) {
        alert("Passwords do not match");
    }
}

document.querySelector("form").addEventListener("submit", validateForm);
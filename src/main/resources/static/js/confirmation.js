function includeHtml() {
    const header = document.querySelector("div#header");
    const footer = document.querySelector("div#footer");

    fetch("/header.html")
        .then(res => res.text())
        .then(data => {
            header.innerHTML = data;
        })

    fetch("/footer.html")
        .then(res => res.text())
        .then(data => {
            footer.innerHTML = data;
        })
}

includeHtml();
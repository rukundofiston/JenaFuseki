function autoHeight(str) {
    var iframe = document.getElementById(str);
    if (iframe.Document) {
        iframe.style.height = iframe.Document.documentElement.scrollHeight;
    } else if (iframe.contentDocument) {
        iframe.height = iframe.contentDocument.body.offsetHeight;
    }
}
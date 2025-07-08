document.getElementById("esqueciSenhaForm").addEventListener("submit", function (event) {
    event.preventDefault();
    const email = document.getElementById("email").value;
    const tipo = document.getElementById("tipo").value;
    const submitButton = event.target.querySelector("button");

    // Verifica se o email não está vazio
    if (!email) {
        document.getElementById("mensagem").textContent = "Por favor, informe seu e-mail.";
        return;
    }

    submitButton.disabled = true;

    fetch("http://localhost:8080/esqueceu/esqueci-senha", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded"
      },
      body: new URLSearchParams({ email, tipo })
    })
    .then(response => {
      if (response.ok) {
        alert("E-mail enviado com sucesso!");
            window.location.href = "resetarSenha.html"
      } else {
        document.getElementById("mensagem").textContent = "Erro ao enviar e-mail. Tente novamente.";
      }
    })
    .catch(() => {
      document.getElementById("mensagem").textContent = "Erro ao enviar e-mail.";
    })
    .finally(() => {
        setTimeout(() => {
            submitButton.disabled = false;
        }, 1000);
    });
});

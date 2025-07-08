document.getElementById("resetarSenhaForm").addEventListener("submit", function (event) {
    event.preventDefault();

    const token = document.getElementById("token").value;
    const novaSenha = document.getElementById("novaSenha").value;

    // Verifica se ambos os campos foram preenchidos
    if (!token || !novaSenha) {
        document.getElementById("mensagem").textContent = "Por favor, preencha todos os campos.";
        return;
    }

    // Envia a requisição POST para redefinir a senha
    fetch("http://localhost:8080/esqueceu/resetar-senha", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({ token, novaSenha })
    })
    .then(response => response.text())
    .then(data => {
        document.getElementById("mensagem").textContent = data;
    })
    .catch(() => {
        document.getElementById("mensagem").textContent = "Erro ao redefinir senha.";
    });
});

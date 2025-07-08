// No arquivo script.js

document.getElementById("redirect").addEventListener("click", async function(event) {
    event.preventDefault(); // Previne o envio padrão do formulário
    const nome = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const senha = document.getElementById("password").value;
    const confirmarSenha = document.getElementById("confirm-password").value;

    if (senha !== confirmarSenha) {
        alert("As senhas não coincidem!");
        return;
    }

  
    const dadosParaEnviar = {
        nomeCliente: nome,
        emailCliente: email,
        senhaCliente: senha,
        confirmarSenhaCliente: confirmarSenha
    };


    try {
        const response = await fetch("http://localhost:8080/cliente", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            // Enviar o objeto corrigido, convertido para string JSON
            body: JSON.stringify(dadosParaEnviar),
        });
    
        if (response.ok) {
            // Se a resposta for bem-sucedida
            alert("Cadastro realizado com sucesso!");
            window.location.href = "../Login/login.html";
        } else {
            // Lógica para tratar o erro de validação do backend
            const errorData = await response.json(); // Tenta obter o JSON do erro
            console.error("Erro ao cadastrar usuário:", errorData);
            
            // Monta uma mensagem de erro mais amigável
            let errorMessages = Object.values(errorData).join("\n");
            alert("Erro ao cadastrar usuário:\n" + errorMessages);
        }
    } catch (error) {
        console.error("Erro na conexão com o servidor:", error);
        alert("Erro na conexão com o servidor.");
    }
});
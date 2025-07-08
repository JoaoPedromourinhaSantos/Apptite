// Seu arquivo para cadastro de restaurante

document.getElementById("cadastro-form").addEventListener("submit", async function(event) {
    event.preventDefault();

    // Coleta de dados (seu código aqui está ótimo)
    const nome = document.getElementById("nome").value;
    const cnpj = document.getElementById("cnpj").value;
    const email = document.getElementById("email").value;
    const cidade = document.getElementById("cidade").value;
    const senha = document.getElementById("senha").value;
    const rua = document.getElementById("rua").value;
    const bairro = document.getElementById("bairro").value;
    const numero = document.getElementById("numero").value;
    const estado = document.getElementById("estado").value;
    const confirmaSenha = document.getElementById("confirmaSenha").value;

    // Validação no frontend (seu código aqui está ótimo)
    if (senha !== confirmaSenha) {
        alert("As senhas não coincidem!");
        return;
    }

    // --- OBJETO DE DADOS CORRIGIDO ---
    const dados = {
        nomeRestaurante: nome,                 // << CORRIGIDO
        cnpj: cnpj,
        emailRestaurante: email,               // << CORRIGIDO
        senhaRestaurante: senha,               // << CORRIGIDO
        confirmarSenhaRestaurante: confirmaSenha, // << ADICIONADO E CORRIGIDO
        ruaEndereco: rua,
        bairroEndereco: bairro,
        numeroEndereco: numero,
        cidadeEndereco: cidade,
        estadoEndereco: estado
    };

    try {
        const resposta = await fetch("http://localhost:8080/restaurante/cadastro", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dados)
        });

        if (resposta.ok) {
            alert("Cadastro realizado com sucesso!");
            window.location.href = "../Login/login.html";
        } else {
            // Lógica de erro melhorada para mostrar mensagens de validação
            const errorData = await resposta.json();
            const errorMessages = Object.values(errorData).join("\n");
            alert("Erro no cadastro:\n" + errorMessages);
        }

    } catch (erro) {
        console.error("Erro ao enviar requisição:", erro);
        alert("Erro de conexão com o servidor.");
    }
});
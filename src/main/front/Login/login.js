// Remove logins antigos ao carregar a página para evitar conflitos
localStorage.removeItem("restauranteLogado");
localStorage.removeItem("usuarioLogado");

document.getElementById("loginButton").addEventListener("click", async function(event) {
    event.preventDefault();

    const emailInput = document.getElementById("email").value;
    const senhaInput = document.getElementById("senha").value;
    const tipo = document.getElementById("tipo").value;

    let url = "";
    if (tipo === "usuario") {
        url = "http://localhost:8080/cliente/login";
    } else if (tipo === "estabelecimento") {
        url = "http://localhost:8080/restaurante/login";
    } else {
        alert("Selecione o tipo de conta.");
        return;
    }

    // 1. O corpo da requisição é sempre o mesmo, como a API espera
    const body = {
        email: emailInput,
        senha: senhaInput
    };

    try {
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(body)
        });

        if (!response.ok) {
            const errorText = await response.text();
            alert("Falha no login: " + (errorText || `Status ${response.status}`));
            return;
        }

        // 2. Salva os dados no localStorage com a chave CORRETA
        if (tipo === "usuario") {
            const clienteLogado = await response.json();
            // Salva usando a chave "usuarioLogado"
            localStorage.setItem("usuarioLogado", JSON.stringify(clienteLogado));
            window.location.href = "../Perfil - Usuário/perfil_usuario.html";
        } else { // tipo === "estabelecimento"
            const restauranteLogado = await response.json();
            // Salva usando a chave "restauranteLogado" para corresponder ao perfilEst.js
            localStorage.setItem("restauranteLogado", JSON.stringify(restauranteLogado));
            window.location.href = "../Perfil - Estabelecimento/perfil_estabelecimento.html";
        }

    } catch (error) {
        console.error("Erro na conexão com o servidor:", error);
        alert("Erro ao conectar com o servidor.");
    }
});
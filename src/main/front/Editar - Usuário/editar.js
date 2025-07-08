document.addEventListener("DOMContentLoaded", function () {
    const API_BASE_URL = 'http://localhost:8080';
    const usuarioLogado = JSON.parse(localStorage.getItem("usuarioLogado"));

    // Verifica se há um usuário logado, senão redireciona
    if (!usuarioLogado || !usuarioLogado.idCliente) {
        alert("Sessão inválida. Por favor, faça login novamente.");
        window.location.href = "../Login/login.html";
        return;
    }

    // --- PARTE 1: Preenche o formulário com os dados corretos ---
    document.getElementById("name").value = usuarioLogado.nomeCliente || "";          // << CORRIGIDO
    document.getElementById("email").value = usuarioLogado.emailCliente || "";        // << CORRIGIDO
    document.getElementById("telefone").value = usuarioLogado.telefoneCliente || "";  // << CORRIGIDO
    document.getElementById("description").value = usuarioLogado.descricaoCliente || ""; // << CORRIGIDO
    // A senha fica vazia por segurança, para o usuário digitar se quiser alterar

    // Lógica para mostrar nome do arquivo (seu código aqui está bom, sem alterações)
    let fileInputs = document.querySelectorAll("input[type='file']");
    fileInputs.forEach(input => {
        input.addEventListener("change", function () {
            // ... seu código para exibir o nome do arquivo ...
        });
    });

    // --- PARTE 2: Envio do formulário de edição ---
    const confirmarBtn = document.getElementById("confirmarBtn"); // Certifique-se que o ID do botão está correto
    if (confirmarBtn) {
        confirmarBtn.addEventListener("click", async function (e) {
            e.preventDefault();

            const idCliente = usuarioLogado.idCliente; // << CORRIGIDO (pega o ID da forma correta)

            // Coleta os dados dos campos do formulário
            const nome = document.getElementById("name").value;
            const email = document.getElementById("email").value;
            const telefone = document.getElementById("telefone").value;
            const senha = document.getElementById("password").value;
            const confirmarSenha = document.getElementById("confirm-password").value;
            const descricao = document.getElementById("description").value;
            const fileInput = document.getElementById("perfil");

            // Validação de senha no frontend
            if (senha && senha !== confirmarSenha) {
                alert("As senhas não coincidem!");
                return;
            }

            const formData = new FormData();
            // << CORRIGIDO: Nomes dos campos correspondem ao EditarClienteRequestDTO
            formData.append("nomeCliente", nome);
            formData.append("emailCliente", email);
            formData.append("telefoneCliente", telefone);
            formData.append("descricaoCliente", descricao);
            if (senha) { // Apenas envia a senha se o campo foi preenchido
                formData.append("senhaCliente", senha);
                formData.append("confirmarSenhaCliente", confirmarSenha);
            }
            if (fileInput.files.length > 0) {
                formData.append("imagem", fileInput.files[0]); // O backend espera o param "imagem"
            }

            // << CORRIGIDO: Endpoint e método HTTP
            fetch(`${API_BASE_URL}/cliente/editar/${idCliente}`, {
                method: "PUT",
                body: formData // Para multipart/form-data, não definimos o Content-Type, o navegador faz isso.
            })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.text().then(text => { throw new Error(text || "Erro desconhecido do servidor."); });
                }
            })
            .then(dataAtualizada => { // dataAtualizada é o ClienteResponseDTO
                alert("Informações salvas com sucesso!");
                
                // << CORRIGIDO: Atualiza o localStorage com os dados retornados pela API
                // Isso garante que todos os dados (incluindo o novo caminho da foto) estejam corretos.
                localStorage.setItem("usuarioLogado", JSON.stringify(dataAtualizada));

                window.location.href = "../Perfil - Usuário/perfil_usuario.html";
            })
            .catch(error => {
                console.error("Erro ao atualizar perfil:", error);
                alert("Erro ao atualizar perfil: " + error.message);
            });
        });
    }

    // --- PARTE 3: Lógica do Modal e Exclusão de Conta ---
    const modal = document.getElementById("myModal");
    const openModalBtn = document.getElementById("openModal");
    const closeModalBtn = document.getElementById("closeModal");
    const deleteAccountBtn = document.getElementById("deleteAccount");

    if (modal && openModalBtn && closeModalBtn && deleteAccountBtn) {
        modal.style.display = "none";

        openModalBtn.addEventListener("click", () => modal.style.display = "block");
        closeModalBtn.addEventListener("click", () => modal.style.display = "none");
        window.addEventListener("click", (event) => {
            if (event.target === modal) {
                modal.style.display = "none";
            }
        });

        deleteAccountBtn.addEventListener("click", function () {
            const idCliente = usuarioLogado.idCliente; // << CORRIGIDO
            if (!idCliente) {
                alert("Erro: Não foi possível identificar o usuário.");
                return;
            }

            
            fetch(`${API_BASE_URL}/cliente/${idCliente}`, {
                method: "DELETE"
            })
            .then(response => {
                if (response.ok) {
                    localStorage.removeItem("usuarioLogado");
                    alert("Conta excluída com sucesso!");
                    window.location.href = "../Login/login.html";
                } else {
                    alert("Erro ao excluir a conta. Tente novamente.");
                }
            })
            .catch(error => {
                console.error("Erro ao excluir conta:", error);
                alert("Erro ao tentar excluir a conta.");
            });
        });
    }
});
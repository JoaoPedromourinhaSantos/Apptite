document.addEventListener("DOMContentLoaded", function() {
    const confirmarBtn = document.getElementById("confirmarAvaliacao");
    const comentarioInput = document.getElementById("comentarioAvaliacao");
    const fotoInput = document.getElementById("fotoAvaliacao"); // Manter, mas funcionalidade de upload desabilitada por enquanto
    const estrelasContainer = document.getElementById("estrelasAvaliacao");
    const estrelas = estrelasContainer.querySelectorAll(".star");
    const mensagemFeedback = document.getElementById("mensagem-feedback");
    const closePopupBtn = document.getElementById("closePopupBtn");
    const nomeUsuarioSpan = document.getElementById("nomeUsuarioAvaliacao");

    let notaSelecionada = 0;

    // Obter o ID do estabelecimento da URL
    const urlParams = new URLSearchParams(window.location.search);
    const idEstabelecimento = urlParams.get('idEstabelecimento'); // Lendo da URL

    // 1. Validar se o ID do restaurante foi passado e é válido
    if (!idEstabelecimento) {
        mensagemFeedback.classList.remove("mensagem-oculta");
        mensagemFeedback.textContent = "Erro: ID do restaurante não fornecido. Não é possível criar a avaliação.";
        mensagemFeedback.style.color = "red";
        confirmarBtn.disabled = true; // Desabilita o botão de confirmar
        // Opcional: esconder o formulário e mostrar uma mensagem
        document.querySelector('.popup-content').innerHTML = '<p style="text-align:center; color:red;">Não foi possível carregar o formulário de avaliação. ID do restaurante não encontrado.</p>';
        return; // Sai da função
    }

    const idEstabelecimentoNum = parseInt(idEstabelecimento, 10);
    if (isNaN(idEstabelecimentoNum)) {
        mensagemFeedback.classList.remove("mensagem-oculta");
        mensagemFeedback.textContent = "Erro: ID do restaurante inválido. Não é possível criar a avaliação.";
        mensagemFeedback.style.color = "red";
        confirmarBtn.disabled = true; // Desabilita o botão de confirmar
        document.querySelector('.popup-content').innerHTML = '<p style="text-align:center; color:red;">Não foi possível carregar o formulário de avaliação. ID do restaurante inválido.</p>';
        return;
    }


    // 2. Preencher nome do usuário logado (se houver)
    const usuarioLogado = JSON.parse(localStorage.getItem("usuarioLogado"));
    if (usuarioLogado && usuarioLogado.nomeCliente) { // Usar 'nomeCliente'
        nomeUsuarioSpan.textContent = usuarioLogado.nomeCliente;
    } else {
        // Se o usuário não estiver logado, não pode avaliar
        mensagemFeedback.classList.remove("mensagem-oculta");
        mensagemFeedback.textContent = "Você precisa estar logado para criar uma avaliação.";
        mensagemFeedback.style.color = "orange";
        confirmarBtn.disabled = true;
        // Opcional: redirecionar para login
        // setTimeout(() => { window.location.href = "../Login/login.html"; }, 2000);
    }

    // Lógica para seleção de estrelas
    estrelas.forEach(star => {
        star.addEventListener("click", function() {
            notaSelecionada = parseInt(this.dataset.value);
            estrelas.forEach((s, index) => {
                s.style.color = index < notaSelecionada ? "gold" : "black"; // Altera a cor das estrelas
            });
        });
        // Para hover (opcional)
        star.addEventListener("mouseover", function() {
            const hoverValue = parseInt(this.dataset.value);
            estrelas.forEach((s, index) => {
                s.style.color = index < hoverValue ? "gold" : "black";
            });
        });
        star.addEventListener("mouseout", function() {
            estrelas.forEach((s, index) => {
                s.style.color = index < notaSelecionada ? "gold" : "black";
            });
        });
    });

    confirmarBtn.addEventListener("click", async function() {
        const idCliente = usuarioLogado ? usuarioLogado.idCliente : null;

        const comentario = comentarioInput.value;
        // const foto = fotoInput.files[0]; // Funcionalidade de foto comentada por enquanto

        // 3. Validações antes de enviar
        if (!idCliente) {
            alert("Erro: Não foi possível identificar o usuário logado.");
            return;
        }
        if (notaSelecionada === 0) {
            alert("Por favor, selecione uma nota para a avaliação.");
            return;
        }
        // idEstabelecimentoNum já é validado no início do script.

        // Preparar os dados para o backend conforme o AvaliacaoInputDTO
        const avaliacaoData = {
            idRestaurante: idEstabelecimentoNum, // ID do restaurante dentro do DTO
            nota: notaSelecionada,
            comentario: comentario
        };

        try {
            // A API espera o idCliente como @RequestParam na URL
            const response = await fetch(`http://localhost:8080/avaliacoes?idCliente=${idCliente}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json", // IMPORTANTE: Enviar como JSON
                    "Accept": "application/json"
                },
                body: JSON.stringify(avaliacaoData), // Corpo da requisição como JSON
            });

            if (response.ok) {
                mensagemFeedback.classList.remove("mensagem-oculta");
                mensagemFeedback.textContent = "Avaliação enviada com sucesso!";
                mensagemFeedback.style.color = "green";

                // Opcional: Limpar formulário após sucesso
                comentarioInput.value = "";
                fotoInput.value = ""; // Limpa o input de arquivo também
                notaSelecionada = 0;
                estrelas.forEach(s => s.style.color = "black");

                setTimeout(() => {
                    // Redireciona de volta para a página de avaliações do restaurante específico
                    window.location.href = `avaliacao.html?id=${idEstabelecimentoNum}`; // Usar 'id' para consistência
                }, 1500); // Espera 1.5 segundos
            } else {
                const errorData = await response.json().catch(() => response.text()); // Tenta ler como JSON, se falhar, lê como texto
                let errorMessage = "Erro desconhecido ao enviar avaliação.";

                if (typeof errorData === 'object' && errorData !== null) {
                    // Se for um mapa de erros (vindo do @Valid)
                    errorMessage = Object.values(errorData).join("\n");
                } else if (typeof errorData === 'string' && errorData.length > 0) {
                    // Se for uma string de erro (como "Cliente já avaliou este restaurante.")
                    errorMessage = errorData;
                } else {
                    errorMessage = `Erro ao enviar avaliação: Status ${response.status} - ${response.statusText}`;
                }

                mensagemFeedback.classList.remove("mensagem-oculta");
                mensagemFeedback.textContent = "Erro ao enviar avaliação: " + errorMessage;
                mensagemFeedback.style.color = "red";
                console.error("Erro ao enviar avaliação:", errorMessage);
            }
        } catch (error) {
            console.error("Erro na conexão com o servidor:", error);
            mensagemFeedback.classList.remove("mensagem-oculta");
            mensagemFeedback.textContent = "Erro de conexão com o servidor.";
            mensagemFeedback.style.color = "red";
        }
    });

    // Lógica para fechar o popup
    closePopupBtn.addEventListener("click", function() {
        // Volta para a página de avaliações do restaurante específico
        window.location.href = `avaliacao.html?id=${idEstabelecimentoNum}`; // Usar 'id' para consistência
    });
});
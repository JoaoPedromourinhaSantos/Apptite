document.addEventListener("DOMContentLoaded", async function() {
    const urlParams = new URLSearchParams(window.location.search);
    const idEstabelecimento = urlParams.get('id'); // Alterado para 'id' para consistência com perfil_restaurante.html

    const nomeEstabelecimentoSpan = document.getElementById("nomeEstabelecimentoAvaliacoes");
    const avaliacoesContainer = document.getElementById("avaliacoesContainer");
    const criarAvaliacaoLink = document.getElementById("criarAvaliacaoLink"); // Novo ID para o link do botão

    // Se não houver idEstabelecimento na URL, tente obter do localStorage (se for a página de perfil de estabelecimento logado)
    // Isso é mais para compatibilidade, o ideal é sempre passar via URL
    if (!idEstabelecimento) {
        const restauranteLogado = JSON.parse(localStorage.getItem("restauranteLogado"));
        if (restauranteLogado && restauranteLogado.idRestaurante) {
            idEstabelecimento = restauranteLogado.idRestaurante;
            // Se o ID vier do localStorage, a URL não terá o 'id' parâmetro, então precisamos ajustar o link de voltar se houver.
        } else {
            // Se ainda não tiver ID, desabilita a funcionalidade
            nomeEstabelecimentoSpan.textContent = "Erro: ID do estabelecimento não fornecido.";
            avaliacoesContainer.innerHTML = "<p>Não foi possível carregar as avaliações sem um ID de estabelecimento válido.</p>";
            if (criarAvaliacaoLink) criarAvaliacaoLink.style.display = 'none'; // Esconde o botão de criar avaliação
            document.querySelector(".voltar").style.display = 'none'; // Esconde o botão de voltar, se aplicável
            return; // Sai da função
        }
    }

    // Configura o link do botão "CRIAR AVALIAÇÃO" para incluir o idEstabelecimento
    if (criarAvaliacaoLink) {
        criarAvaliacaoLink.href = `popUp.html?idEstabelecimento=${idEstabelecimento}`;
    }

    // Tentar obter o nome do estabelecimento para exibição
    try {
        const estResponse = await fetch(`http://localhost:8080/restaurante/${idEstabelecimento}`); // Endpoint correto para restaurante
        if (estResponse.ok) {
            const estabelecimento = await estResponse.json();
            nomeEstabelecimentoSpan.textContent = estabelecimento.nomeRestaurante; // Propriedade correta
        } else {
            nomeEstabelecimentoSpan.textContent = "Estabelecimento Desconhecido";
            console.error("Erro ao buscar nome do estabelecimento:", estResponse.statusText);
        }
    } catch (error) {
        nomeEstabelecimentoSpan.textContent = "Estabelecimento Desconhecido";
        console.error("Erro na conexão ao buscar nome do estabelecimento:", error);
    }

    // Função auxiliar para renderizar estrelas
    function renderStars(nota) {
        let starsHTML = '';
        const notaNum = parseFloat(nota) || 0;
        for (let i = 1; i <= 5; i++) {
            starsHTML += i <= notaNum ? '★' : '☆';
        }
        return `<span style="color: gold;">${starsHTML}</span>`; // Estilo in-line para garantir cor dourada
    }


    // Buscar e exibir avaliações
    try {
        const response = await fetch(`http://localhost:8080/avaliacoes/restaurante/${idEstabelecimento}`);
        if (response.ok) {
            const avaliacoes = await response.json(); // reviews é uma lista de AvaliacaoOutputDTO
            if (avaliacoes.length === 0) {
                avaliacoesContainer.innerHTML = "<p>Nenhuma avaliação encontrada para este estabelecimento.</p>";
            } else {
                avaliacoesContainer.innerHTML = ""; // Limpa o conteúdo antes de adicionar
                avaliacoes.forEach(avaliacao => {
                    const avaliacaoDiv = document.createElement("div");
                    avaliacaoDiv.classList.add("avaliacao");
                    
                    // Note: Foto de avaliação não está no AvaliacaoOutputDTO nem no fluxo atual do backend.
                    // Removido o campo `fotoAvaliacao` por não ser suportado atualmente pelo DTO e backend para avaliações.
                    avaliacaoDiv.innerHTML = `
                        <div class="usuario">
                            <span class="icone">&#128100;</span>
                            <span class="nome">${avaliacao.nomeCliente || 'Usuário Anônimo'}</span> <span class="estrelas">${renderStars(avaliacao.nota)}</span> </div>
                        <div class="descricao">
                            <p>${avaliacao.comentario || 'Sem comentário.'}</p>
                            ${avaliacao.respostaRestaurante ? `<div class="resposta-restaurante"><strong>Resposta do Restaurante:</strong> ${avaliacao.respostaRestaurante}</div>` : ''}
                        </div>
                    `;
                    avaliacoesContainer.appendChild(avaliacaoDiv);
                });
            }
        } else if (response.status === 204) { // No Content
            avaliacoesContainer.innerHTML = "<p>Nenhuma avaliação encontrada para este estabelecimento.</p>";
        }
        else {
            avaliacoesContainer.innerHTML = "<p>Erro ao carregar avaliações.</p>";
            console.error("Erro ao buscar avaliações:", response.statusText);
        }
    } catch (error) {
        avaliacoesContainer.innerHTML = "<p>Erro de conexão ao carregar avaliações.</p>";
        console.error("Erro de conexão ao buscar avaliações:", error);
    }
});

// A função de "Voltar" na barra superior
document.querySelector(".voltar").addEventListener("click", function(event) {
    event.preventDefault();
    window.history.back(); // Volta para a página anterior
});
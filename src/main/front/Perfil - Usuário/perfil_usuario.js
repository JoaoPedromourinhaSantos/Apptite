// perfil_usuario.js

document.addEventListener("DOMContentLoaded", () => {
    // 1. Pega os dados do usuário do localStorage
    const data = localStorage.getItem("usuarioLogado");
    if (!data) {
        alert("Você precisa fazer login primeiro!");
        window.location.href = "../Login/login.html";
        return;
    }
    const usuarioLogado = JSON.parse(data);
    const API_BASE_URL = 'http://localhost:8080';

    // 2. Extrai o ID do cliente usando o nome de propriedade correto
    const idCliente = usuarioLogado ? usuarioLogado.idCliente : null; // << CORREÇÃO PRINCIPAL

    // 3. Verifica se o ID foi encontrado antes de prosseguir
    if (!idCliente) {
        alert("ID do cliente não encontrado. Por favor, faça login novamente.");
        window.location.href = "../Login/login.html";
        return;
    }

    // Configuração do botão de logout
    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", () => {
            localStorage.removeItem("usuarioLogado");
            window.location.href = "../Login/login.html";
        });
    }

    // 4. Busca e exibe os dados do perfil usando as propriedades corretas do DTO
    // O fetch para os dados do perfil agora usa o idCliente correto
    fetch(`${API_BASE_URL}/cliente/${idCliente}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Falha ao carregar dados do cliente.');
            }
            return response.json();
        })
        .then(clienteData => { // clienteData é o ClienteResponseDTO
            document.getElementById("nomeUsuario").innerText = clienteData.nomeCliente || "Nome não disponível"; // << CORRIGIDO
            document.getElementById("descricaoUsuario").innerText = clienteData.descricaoCliente || "Sem descrição."; // << CORRIGIDO
            
            const imagem = document.querySelector(".profile-pic");
            if (imagem) {
                if (clienteData.fotoCliente) { // << CORRIGIDO
                    // Assumindo que fotoCliente é um caminho relativo que o backend serve
                    imagem.src = `${API_BASE_URL}/${clienteData.fotoCliente}`;
                } else {
                    imagem.alt = "Sem foto de perfil";
                }
            }
        })
        .catch(error => {
            console.error("Erro ao carregar perfil do cliente:", error);
            document.getElementById("nomeUsuario").innerText = "Erro ao carregar nome";
            document.getElementById("descricaoUsuario").innerText = "Erro ao carregar descrição";
        });

    // 5. Busca e exibe as avaliações do cliente
    fetchAndDisplayMinhasAvaliacoes(idCliente);
});


async function fetchAndDisplayMinhasAvaliacoes(idCliente) {
    const minhasAvaliacoesListDiv = document.getElementById('lista-minhas-avaliacoes');
    if (!minhasAvaliacoesListDiv) return;

    minhasAvaliacoesListDiv.innerHTML = '<p>Carregando suas avaliações...</p>';
    const API_BASE_URL = 'http://localhost:8080';

    try {
        // A URL do endpoint já estava correta, mas agora usamos idCliente
        const response = await fetch(`${API_BASE_URL}/avaliacoes/cliente/${idCliente}`);
        
        if (!response.ok) {
            if (response.status === 204) { // 204 No Content
                minhasAvaliacoesListDiv.innerHTML = '<p>Você ainda não fez nenhuma avaliação.</p>';
                return;
            }
            throw new Error(`Erro ao buscar suas avaliações: ${response.status}`);
        }
        
        const reviews = await response.json(); // reviews é uma lista de AvaliacaoOutputDTO

        if (reviews.length === 0) {
            minhasAvaliacoesListDiv.innerHTML = '<p>Você ainda não fez nenhuma avaliação.</p>';
            return;
        }

        minhasAvaliacoesListDiv.innerHTML = ''; // Limpa "Carregando..."
        
        reviews.forEach(review => {
            const reviewItemDiv = document.createElement('div');
            reviewItemDiv.classList.add('box-avaliacao');
            
            const dataFormatada = new Date(review.dataAvaliacao).toLocaleDateString('pt-BR');

            // 6. Usa as propriedades corretas do AvaliacaoOutputDTO
            reviewItemDiv.innerHTML = `
                <h3>${review.nomeRestaurante || `Restaurante ID: ${review.idRestaurante}`}</h3> <div>${renderStars(review.nota)}</div>
                <p>${review.comentario || ''}</p>
                ${review.respostaRestaurante ? `
                    <div class="establishment-reply-container">
                        <p class="establishment-reply-header">Resposta do Restaurante:</p>
                        <p class="establishment-reply-text">${review.respostaRestaurante}</p> </div>
                ` : ''}
                <p style="font-size: 0.8em; color: #777;">Avaliado em: ${dataFormatada}</p>
            `;

            minhasAvaliacoesListDiv.appendChild(reviewItemDiv);
        });

    } catch (error) {
        console.error('Falha ao carregar suas avaliações:', error);
        minhasAvaliacoesListDiv.innerHTML = `<p>Nenhuma avaliação ainda.</p>`;
    }
}

function renderStars(nota) {
    let starsHTML = '';
    const notaNum = nota || 0;
    for (let i = 1; i <= 5; i++) {
        starsHTML += i <= notaNum ? '★' : '☆';
    }
    return `<span style="color: #FFD700; font-size: 1.2em;">${starsHTML}</span>`;
}
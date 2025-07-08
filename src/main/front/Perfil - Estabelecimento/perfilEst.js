const API_BASE_URL = 'http://localhost:8080';

// =================================================================================
// FUNÇÕES DE AJUDA
// =================================================================================

/**
 * Renderiza uma representação em estrelas para uma nota numérica.
 * @param {number} nota - A nota de 1 a 5.
 * @returns {string} O HTML das estrelas.
 */
function renderStars(nota) {
    let starsHTML = '';
    const notaNum = parseFloat(nota) || 0;
    for (let i = 1; i <= 5; i++) {
        starsHTML += i <= notaNum ? '★' : '☆';
    }
    return `<span style="color: #FFD700;">${starsHTML}</span>`;
}

/**
 * Abre o popup para responder a uma avaliação, preenchendo os dados.
 */
// Função para abrir o popup de resposta com os dados da avaliação específica
function abrirPopupResposta(idAvaliacao, nomeUsuario, nota, comentarioCliente, respostaExistente = "") {
    const popup = document.getElementById("popupAvaliacao");
    if (!popup) {
        console.error("Popup de resposta #popupAvaliacao não encontrado!");
        return;
    }

    const nomeUsuarioElement = popup.querySelector(".nome-usuario");
    const estrelasElement = popup.querySelector(".estrelas");
    const comentarioClienteTextarea = popup.querySelector("textarea[readonly]");
    const respostaTextarea = popup.querySelector("textarea:not([readonly])");

    if (nomeUsuarioElement) nomeUsuarioElement.textContent = nomeUsuario || "Usuário";
    if (estrelasElement) estrelasElement.innerHTML = renderStars(nota);
    if (comentarioClienteTextarea) comentarioClienteTextarea.value = comentarioCliente || "";
    if (respostaTextarea) {
        respostaTextarea.value = respostaExistente || ""; // Preenche com resposta existente se houver
        respostaTextarea.placeholder = "Digite sua resposta aqui...";
    }

    popup.dataset.idAvaliacao = idAvaliacao; // Armazena o ID da avaliação no popup
    popup.style.display = "flex";
}

/**
 * Fecha o popup de resposta.
 */
function fecharPopup() {
    const popup = document.getElementById("popupAvaliacao");
    if (popup) popup.style.display = "none";
}

/**
 * Envia a resposta do restaurante para a API.
 */
async function confirmarResposta() {
    const popup = document.getElementById("popupAvaliacao");
    if (!popup) return;

    const idAvaliacao = popup.dataset.idAvaliacao;
    const textoResposta = popup.querySelector("textarea:not([readonly])").value.trim();
    if (!idAvaliacao || !textoResposta) {
        alert("Por favor, digite uma resposta.");
        return;
    }

    const restauranteLogado = JSON.parse(localStorage.getItem("restauranteLogado"));
    const idRestauranteLogado = restauranteLogado ? restauranteLogado.idRestaurante : null;
    if (!idRestauranteLogado) {
        alert("Erro: ID do Restaurante não encontrado. Faça login novamente.");
        return;
    }

    try {
        const url = `${API_BASE_URL}/avaliacoes/${idAvaliacao}/responder?idRestauranteLogado=${idRestauranteLogado}`;
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ textoResposta })
        });

        if (!response.ok) throw new Error(await response.text() || 'Falha ao enviar resposta');

        alert("Resposta enviada com sucesso!");
        fecharPopup();
        fetchAndDisplayReviews(idRestauranteLogado); // Recarrega as avaliações
    } catch (error) {
        console.error("Erro ao enviar resposta:", error);
        alert(`Erro ao enviar resposta: ${error.message}`);
    }
}

// =================================================================================
// NOVA LÓGICA PARA O POP-UP DE FILTROS
// =================================================================================

/**
 * Exibe os filtros que o restaurante já possui na página de perfil.
 * @param {Array} filtros - A lista de filtros do restaurante.
 */
function displayActiveFilters(filtros = []) {
    const container = document.getElementById("filtrosAtivos");
    container.innerHTML = ""; // Limpa o conteúdo anterior

    if (!filtros || filtros.length === 0) {
        container.innerHTML = "<p>Nenhum filtro selecionado.</p>";
        return;
    }

    filtros.forEach(filtro => {
        const tag = document.createElement("span");
        tag.className = "filtro-tag";
        tag.textContent = filtro.nomeFiltro;
        container.appendChild(tag);
    });
}


/**
 * Busca todos os filtros disponíveis na API e preenche o pop-up.
 * @param {Array<number>} selectedIds - IDs dos filtros já selecionados pelo restaurante.
 */
async function populateFiltersPopup(selectedIds = []) {
    const container = document.getElementById("listaFiltrosPopup");
    container.innerHTML = '<p>Carregando filtros...</p>';

    try {
        const response = await fetch(`${API_BASE_URL}/filtros`);
        if (!response.ok) throw new Error("Não foi possível carregar os filtros.");

        const allFilters = await response.json();
        container.innerHTML = ""; // Limpa o container

        allFilters.forEach(filtro => {
            const isChecked = selectedIds.includes(filtro.idFiltro);
            const itemDiv = document.createElement('div');
            itemDiv.className = 'filtro-item';
            itemDiv.innerHTML = `
                <input type="checkbox" id="filtro-${filtro.idFiltro}" value="${filtro.idFiltro}" ${isChecked ? 'checked' : ''}>
                <label for="filtro-${filtro.idFiltro}">${filtro.nomeFiltro}</label>
            `;
            container.appendChild(itemDiv);
        });
    } catch (error) {
        console.error("Erro ao popular filtros:", error);
        container.innerHTML = "<p>Erro ao carregar filtros. Tente novamente.</p>";
    }
}


/**
 * Abre o popup de filtros e inicia o carregamento das opções.
 */
function abrirPopupFiltros() {
    const restaurante = JSON.parse(localStorage.getItem("restauranteLogado"));
    // Extrai os IDs dos filtros do restaurante para pré-selecionar os checkboxes
    const selectedIds = restaurante.filtros ? restaurante.filtros.map(f => f.idFiltro) : [];
    
    populateFiltersPopup(selectedIds);
    document.getElementById("popupFiltros").style.display = "flex";
}


/**
 * Fecha o popup de filtros.
 */
function fecharPopupFiltros() {
    document.getElementById("popupFiltros").style.display = "none";
}

/**
 * Salva os filtros selecionados no backend.
 */
async function salvarFiltros() {
    const restaurante = JSON.parse(localStorage.getItem("restauranteLogado"));
    const idRestaurante = restaurante.idRestaurante;

    const selectedCheckboxes = document.querySelectorAll('#listaFiltrosPopup input[type="checkbox"]:checked');
    const selectedIds = Array.from(selectedCheckboxes).map(cb => parseInt(cb.value));

    // O backend espera um FormData porque o endpoint de edição também lida com upload de arquivos.
    // Mesmo que não estejamos enviando arquivos aqui, precisamos usar FormData.
    const formData = new FormData();
    selectedIds.forEach(id => {
        formData.append('filtroIds', id);
    });

    try {
        const response = await fetch(`${API_BASE_URL}/restaurante/editar/${idRestaurante}`, {
            method: 'PUT',
            body: formData 
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Falha ao salvar os filtros.');
        }

        const restauranteAtualizado = await response.json();
        // Atualiza o localStorage com os novos dados do restaurante
        localStorage.setItem('restauranteLogado', JSON.stringify(restauranteAtualizado));

        alert("Filtros atualizados com sucesso!");
        fecharPopupFiltros();
        displayActiveFilters(restauranteAtualizado.filtros); // Atualiza a exibição na página

    } catch (error) {
        console.error("Erro ao salvar filtros:", error);
        alert(`Erro: ${error.message}`);
    }
}

/**
 * Busca e exibe as avaliações recebidas pelo restaurante.
 * @param {number} idRestaurante - O ID do restaurante.
 */
async function fetchAndDisplayReviews(idRestaurante) {
    const reviewsListDiv = document.getElementById('lista-avaliacoes-recebidas');
    if (!reviewsListDiv) return;

    reviewsListDiv.innerHTML = '<p>Carregando avaliações...</p>';
    try {
        const response = await fetch(`${API_BASE_URL}/avaliacoes/restaurante/${idRestaurante}`);
        if (!response.ok) {
            if (response.status === 204) {
                reviewsListDiv.innerHTML = '<p>Nenhuma avaliação recebida ainda.</p>';
            }
            return;
        }
        const reviews = await response.json();
        reviewsListDiv.innerHTML = '';
        if (reviews.length === 0) {
            reviewsListDiv.innerHTML = '<p>Nenhuma avaliação recebida ainda.</p>';
            return;
        }
        reviews.forEach(review => {
            const reviewItem = document.createElement('section');
            reviewItem.className = 'comentario';
            const dataFormatada = new Date(review.dataAvaliacao).toLocaleDateString('pt-BR');
            const respostaHTML = review.respostaRestaurante ? `
                <div class="establishment-reply-container">
                    <p class="establishment-reply-header">Nossa Resposta:</p>
                    <p class="establishment-reply-text">${review.respostaRestaurante}</p>
                </div>` : '';
            const botaoTexto = review.respostaRestaurante ? 'Editar Resposta' : 'Responder';
            
            reviewItem.innerHTML = `
                <p><strong>${review.nomeCliente || 'Anônimo'}</strong> <span style="font-size:0.8em; color:#555;">em ${dataFormatada}</span></p>
                <p>${renderStars(review.nota)}</p>
                <p>${review.comentario || ''}</p>
                ${respostaHTML}
                <div class="chat-container" style="text-align: right; margin-top: 10px;">
                    <a href="#" class="chat-button" data-id-avaliacao="${review.idAvaliacao}" data-nome-usuario="${review.nomeCliente}" data-nota="${review.nota}" data-comentario="${review.comentario}" data-resposta-existente="${review.respostaRestaurante || ''}">${botaoTexto}</a>
                </div>`;
            reviewsListDiv.appendChild(reviewItem);
        });
        
        reviewsListDiv.querySelectorAll('.chat-button').forEach(button => {
            button.addEventListener('click', e => {
                e.preventDefault();
                const data = e.target.dataset;
                abrirPopupResposta(data.idAvaliacao, data.nomeUsuario, data.nota, data.comentario, data.respostaExistente);
            });
        });
    } catch (error) {
        console.error('Falha ao carregar avaliações:', error);
        reviewsListDiv.innerHTML = `<p>Nenhuma avaliação.</p>`;
    }

    async function fetchTaxaResposta(idRestaurante) {
    const elementoTaxa = document.getElementById("taxaRespostaValor");
    if (!elementoTaxa) return;

    elementoTaxa.textContent = "Carregando...";
    try {
        const response = await fetch(`${API_BASE_URL}/avaliacoes/indicadores/taxa-resposta/${idRestaurante}`);
        if (!response.ok) throw new Error('Falha ao buscar dados da taxa de resposta.');

        const data = await response.json();
        const taxa = data.taxaDeResposta || 0;
        elementoTaxa.textContent = `${taxa.toFixed(1)}%`;

    } catch (error) {
        console.error("Erro ao buscar taxa de resposta:", error);
        elementoTaxa.textContent = "N/A";
    }
}

}

// =================================================================================
// FUNÇÃO PRINCIPAL QUE RODA QUANDO A PÁGINA CARREGA
// =================================================================================
document.addEventListener("DOMContentLoaded", () => {
    // 1. Usa a chave correta para ler os dados do restaurante do localStorage
    const data = localStorage.getItem("restauranteLogado"); 

    if (!data) {
        alert("Você precisa fazer login como estabelecimento!");
        window.location.href = "../Login/login.html";
        return;
    }

    const restaurante = JSON.parse(data);
    const idRestaurante = restaurante.idRestaurante;

    if (!idRestaurante) {
        alert("ID do restaurante inválido. Faça login novamente.");
        window.location.href = "../Login/login.html";
        return;
    }

    // 2. Preenche o perfil usando as propriedades corretas do objeto
    document.querySelector(".dadosUser h1").textContent = restaurante.nomeRestaurante || "Nome não informado";
    document.querySelector(".dadosUser .box p:nth-child(2)").textContent = restaurante.descricaoRestaurante || "Descrição não informada";
    
    const mediaPrecoElement = Array.from(document.querySelectorAll(".dadosUser .box p")).find(p => p.textContent.toLowerCase().includes("media de preço"));
    if (mediaPrecoElement) mediaPrecoElement.textContent = `Média de preço: ${restaurante.faixaPreco ? 'R$ ' + restaurante.faixaPreco : "Não informada"}`;
    
    const notaElement = Array.from(document.querySelectorAll(".dadosUser .box p")).find(p => p.textContent.includes("★") || p.textContent.toLowerCase().includes("avaliaç"));
    if (notaElement) {
        const notaFormatada = restaurante.avaliacaoGeral ? restaurante.avaliacaoGeral.toFixed(1) : "N/A";
        notaElement.innerHTML = restaurante.avaliacaoGeral ? `${renderStars(restaurante.avaliacaoGeral)} (${notaFormatada})` : "Sem avaliações ainda";
    }

    const enderecoContainer = document.querySelector(".box-descricao");
    if (enderecoContainer) {
        const psParaRemover = enderecoContainer.querySelectorAll("p");
        psParaRemover.forEach(p => p.remove());
        const linha1 = `${restaurante.ruaEndereco || ""}${restaurante.numeroEndereco ? ', ' + restaurante.numeroEndereco : ''}${restaurante.bairroEndereco ? ' - ' + restaurante.bairroEndereco : ''}`;
        const linha2 = `${restaurante.cidadeEndereco || ""}${restaurante.estadoEndereco ? ', ' + restaurante.estadoEndereco : ''}`;
        if (linha1.trim()) {
            const p1 = document.createElement("p");
            p1.textContent = linha1.trim();
            enderecoContainer.appendChild(p1);
        }
        if (linha2.trim()) {
            const p2 = document.createElement("p");
            p2.textContent = linha2.trim();
            enderecoContainer.appendChild(p2);
        }
    }

    const contatoInfoBox = document.querySelector(".box-contato-info");
    if (contatoInfoBox) {
        contatoInfoBox.querySelector(".telefone-info").textContent = `Telefone: ${restaurante.telefoneRestaurante || "Não informado"}`;
        contatoInfoBox.querySelector(".email-info").textContent = `E-mail: ${restaurante.emailRestaurante || "Não informado"}`;
    }

    const imagemCardapio = document.getElementById("cardapioImg");
    const statusCardapio = document.getElementById("cardapio-status");

    // Verificamos o campo booleano 'temFotoCardapio' que vem da API
    if (restaurante.temFotoCardapio) {
        // Se existir, montamos a URL para o novo endpoint que busca a imagem
        imagemCardapio.src = `${API_BASE_URL}/restaurante/${idRestaurante}/cardapio`;
        
        // Exibimos a imagem e escondemos o texto de status
        imagemCardapio.style.display = 'block';
        statusCardapio.style.display = 'none';
    } else {
        // Se não existir, garantimos que a imagem fique escondida
        // e informamos ao usuário.
        imagemCardapio.style.display = 'none';
        statusCardapio.textContent = 'Nenhuma imagem de cardápio disponível.';
    }

    // EXIBIR FILTROS ATIVOS
    displayActiveFilters(restaurante.filtros);


const horariosDiv = document.getElementById("horariosFuncionamento");
if (horariosDiv) {
    if (idRestaurante) {
        // Usa a URL correta e RESTful da API
        fetch(`${API_BASE_URL}/horarios/restaurante/${idRestaurante}`)
            .then(res => {
                // Trata a resposta do servidor de forma mais robusta
                if (res.ok) return res.json();
                if (res.status === 204) return []; // Se não tiver conteúdo, retorna lista vazia
                throw new Error('Falha ao carregar horários: ' + res.statusText);
            })
            .then(dataHorarios => {
                const diasSemana = ["Domingo", "Segunda", "Terca", "Quarta", "Quinta", "Sexta", "Sabado"];
                horariosDiv.innerHTML = ""; // Limpa a div antes de adicionar os novos horários

                // Preenche com os horários recebidos
                diasSemana.forEach(dia => {
                    const infoDia = dataHorarios.find(h => h.diaSemana === dia);
                    const span = document.createElement("span");
                    
                    let horarioTexto = "Fechado";
                    // Garante que o horário só é exibido se o dia estiver ativo e os horários definidos
                    if (infoDia && infoDia.ativo && infoDia.horaInicio && infoDia.horaFim) {
                        horarioTexto = `${infoDia.horaInicio} - ${infoDia.horaFim}`;
                    }
                    
                    span.innerHTML = `<span class="dia">${dia}</span><span class="hora">${horarioTexto}</span>`;
                    horariosDiv.appendChild(span);
                });
            })
            .catch(err => {
                console.error("Erro ao carregar horários do estabelecimento:", err);
                horariosDiv.innerHTML = "<p>Horários indisponíveis.</p>";
            });
    } else {
        // Mensagem caso o idRestaurante não seja encontrado no localStorage
        horariosDiv.innerHTML = "<p>Não foi possível carregar os horários.</p>";
    }
}

    // Carregar Galeria
    if (document.querySelector(".swiper-wrapper") && restaurante.imagensGaleria) {
        const swiperWrapper = document.querySelector(".swiper-wrapper");
        swiperWrapper.innerHTML = ''; 
        restaurante.imagensGaleria.forEach(imgInfo => {
            const slide = document.createElement('div');
            slide.className = 'swiper-slide';
            slide.innerHTML = `<div class="project-img"><img src="${API_BASE_URL}/restaurante/galeria/imagem/${imgInfo.id}" alt="Foto da galeria"></div>`;
            swiperWrapper.appendChild(slide);
        });
        if (swiperWrapper.swiper) { swiperWrapper.swiper.destroy(true, true); }
        new Swiper('.swiper', { /* ...opções do swiper... */ });
    }

    // Carregar Avaliações
    fetchAndDisplayReviews(idRestaurante);

    // Configurar Listeners de Botões
    const gerenciarReservasBtn = document.getElementById("gerenciarReservasBtn");
    if (gerenciarReservasBtn) {
        gerenciarReservasBtn.addEventListener("click", e => {
            e.preventDefault();
            window.location.href = `../Gerenciar Reservas/gerenciarReservas.html?idRestaurante=${idRestaurante}`;
        });
    }

    const verMetricasBtn = document.getElementById("verMetricasBtn");
    if (verMetricasBtn) {
        verMetricasBtn.addEventListener("click", (e) => {
            e.preventDefault();
            // Redireciona para a mesma página de gerenciamento, passando o ID
            // O usuário poderá então clicar na aba "Métricas"
            window.location.href = `../Indicadores/Indicadores.html?idRestaurante=${idRestaurante}`;
        });
    }

    

    
    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", () => {
            localStorage.removeItem("restauranteLogado");
            window.location.href = "../Login/login.html";
        });
    }


    document.getElementById("editarFiltrosBtn").addEventListener("click", abrirPopupFiltros);
    document.getElementById("fecharPopupFiltrosBtn").addEventListener("click", fecharPopupFiltros);
    document.getElementById("cancelarFiltrosBtn").addEventListener("click", fecharPopupFiltros);
    document.getElementById("salvarFiltrosBtn").addEventListener("click", salvarFiltros);
});
const API_BASE_URL = 'http://localhost:8080';
let currentRestaurantId = null;
let currentImageIndex = 0;

// ========================================================
// 1. DEFINIÇÃO DE TODAS AS FUNÇÕES DE AJUDA
// ========================================================

/**
 * Função para mover o carrossel de imagens.
 */
function moveSlide(step) {
    const imagesContainer = document.querySelector('.carousel-images');
    if (!imagesContainer || !imagesContainer.children.length || imagesContainer.children.length <= 1) return;
    const totalImages = imagesContainer.children.length;
    currentImageIndex = (currentImageIndex + step + totalImages) % totalImages;
    imagesContainer.style.transform = `translateX(${-currentImageIndex * 100}%)`;
}

/**
 * Renderiza uma representação em estrelas para uma nota numérica.
 */
function renderStars(nota) {
    let starsHTML = '';
    const notaNum = parseFloat(nota) || 0;
    const roundedRating = Math.round(notaNum * 2) / 2;
    for (let i = 1; i <= 5; i++) {
        if (i <= roundedRating) {
            starsHTML += '★';
        } else if (i - 0.5 === roundedRating) {
            starsHTML += '★';
        } else {
            starsHTML += '☆';
        }
    }
    return `<span style="color: #FFD700;">${starsHTML}</span>`;
}

/**
 * Funções para controlar o modal de avaliação.
 */
function openReviewModal() {
    const reviewModal = document.getElementById('reviewModal');
    if (reviewModal) {
        document.getElementById('reviewForm').reset();
        reviewModal.style.display = 'block';
    }
}

function closeReviewModal() {
    const reviewModal = document.getElementById('reviewModal');
    if (reviewModal) reviewModal.style.display = 'none';
}

/**
 * Lida com a submissão do formulário de avaliação.
 */
async function handleReviewSubmit(event) {
    event.preventDefault();
    const usuarioLogado = JSON.parse(localStorage.getItem("usuarioLogado"));
    if (!usuarioLogado || !usuarioLogado.idCliente) {
        alert("Você precisa estar logado como cliente para avaliar!");
        return;
    }
    const idCliente = usuarioLogado.idCliente;
    const ratingElement = document.querySelector('input[name="rating"]:checked');
    const nota = ratingElement ? parseInt(ratingElement.value) : null;
    const comentario = document.getElementById('comment').value;

    if (!nota) {
        alert("Por favor, selecione uma nota.");
        return;
    }
    const avaliacaoInput = { idRestaurante: parseInt(currentRestaurantId), nota: nota, comentario: comentario };

    try {
        const response = await fetch(`${API_BASE_URL}/avaliacoes?idCliente=${idCliente}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(avaliacaoInput),
        });
        if (!response.ok) throw new Error(await response.text());
        alert("Avaliação enviada com sucesso!");
        closeReviewModal();
        fetchRestaurantDetails(currentRestaurantId);
        fetchAndDisplayReviews(currentRestaurantId);
        checkIfUserHasReviewed(currentRestaurantId, idCliente);
    } catch (error) {
        alert(`Erro ao enviar avaliação: ${error.message}`);
    }
}

/**
 * Preenche a seção de informações principais do restaurante.
 */
function populateRestaurantInfo(data) {
    document.querySelector('.restaurant-info h1').textContent = data.nomeRestaurante || 'Nome Indisponível';
    document.querySelector('.restaurant-info h1 + p').textContent = data.descricaoRestaurante || 'Descrição Indisponível';
    
    const starsP = Array.from(document.querySelectorAll('.restaurant-info p')).find(p => p.innerHTML.includes('★') || p.textContent.toLowerCase().includes('avaliaç'));
    if (starsP) {
        starsP.innerHTML = data.avaliacaoGeral ? `${renderStars(data.avaliacaoGeral)} (${data.avaliacaoGeral.toFixed(1)})` : 'Sem avaliações';
    }

    const priceP = Array.from(document.querySelectorAll('.restaurant-info p')).find(p => p.textContent.toLowerCase().includes('r$'));
    if (priceP && data.faixaPreco) {
        priceP.textContent = `R$${data.faixaPreco}`;
    } else if (priceP) {
        priceP.textContent = 'R$Não informado';
    }

    const addressStrongElement = Array.from(document.querySelectorAll('.restaurant-info p strong')).find(strong => strong.textContent.includes('Endereço:'));
    if (addressStrongElement) {
        const addressParts = [data.ruaEndereco, data.numeroEndereco, data.bairroEndereco, data.cidadeEndereco, data.estadoEndereco].filter(Boolean).join(', ');
        addressStrongElement.parentElement.innerHTML = `<strong>Endereço:</strong> ${addressParts || 'Não disponível'}`;
    }

    const phoneStrongElement = Array.from(document.querySelectorAll('.restaurant-info p strong')).find(strong => strong.textContent.includes('Telefone de contato:'));
    if (phoneStrongElement) {
        phoneStrongElement.parentElement.innerHTML = `<strong>Telefone de contato:</strong> ${data.telefoneRestaurante || 'Não disponível'}`;
    }
}

/**
 * Preenche o carrossel de imagens da galeria.
 */
function populateCarouselImages(galleryImages) {
    const imagesContainer = document.querySelector('.carousel-images');
    if (!imagesContainer) return;

    // Limpa o container, removendo a imagem placeholder
    imagesContainer.innerHTML = ''; 

    // Caso não haja imagens na galeria
    if (!galleryImages || galleryImages.length === 0) {
        // ▼▼▼ CORREÇÃO AQUI ▼▼▼
        // Criamos um <img> para a imagem padrão, em vez de um <li>
        const placeholderImg = document.createElement('img');
        placeholderImg.src = "../assets/placeholder.jpg"; // Use um caminho válido para uma imagem padrão
        placeholderImg.alt = "Sem fotos na galeria";
        imagesContainer.appendChild(placeholderImg);
        return;
    }
    
    // Ordena as imagens pela propriedade 'ordem'
    galleryImages.sort((a, b) => (a.ordem || 0) - (b.ordem || 0));

    // Adiciona cada imagem da galeria diretamente ao container
    galleryImages.forEach(galleryImage => {
        // ▼▼▼ E CORREÇÃO PRINCIPAL AQUI ▼▼▼
        // Criamos um elemento <img> diretamente
        const imgElement = document.createElement('img');
        
        // Definimos seus atributos
        imgElement.src = `${API_BASE_URL}/restaurante/galeria/imagem/${galleryImage.id}`;
        imgElement.alt = galleryImage.nomeOriginalImagem || 'Foto da Galeria';
        
        // Adicionamos o <img> ao container do carrossel
        imagesContainer.appendChild(imgElement);
    });

    // Reinicia o índice do carrossel para a primeira imagem
    currentImageIndex = 0;
    moveSlide(0);
}

/**
 * Busca e exibe as avaliações do restaurante.
 */
async function fetchAndDisplayReviews(idRestaurante) {
    const reviewsListDiv = document.getElementById('reviews-list');
    if (!reviewsListDiv) return;
    reviewsListDiv.innerHTML = '<p>Carregando avaliações...</p>';
    try {
        const response = await fetch(`${API_BASE_URL}/avaliacoes/restaurante/${idRestaurante}`);
        if (!response.ok && response.status !== 204) throw new Error(`Erro ao buscar avaliações`);
        
        const reviews = response.status === 204 ? [] : await response.json();
        reviewsListDiv.innerHTML = '';
        if (reviews.length === 0) {
            reviewsListDiv.innerHTML = '<p>Nenhuma avaliação ainda. Seja o primeiro a avaliar!</p>';
            return;
        }

        reviews.forEach(review => {
            const reviewElement = document.createElement('div');
            reviewElement.classList.add('review-item');
            const reviewDate = new Date(review.dataAvaliacao).toLocaleDateString('pt-BR');
            let reviewHTML = `
                <div class="review-header">
                    <span class="review-user">${review.nomeCliente || 'Usuário Anônimo'}</span>
                    <span class="review-date">${reviewDate}</span>
                </div>
                <div class="review-stars">${renderStars(review.nota)}</div>
                <p class="review-comment">${review.comentario || ''}</p>`;
            if (review.respostaRestaurante) {
                reviewHTML += `<div class="establishment-reply"><strong>Resposta do Restaurante:</strong><p>${review.respostaRestaurante}</p></div>`;
            }
            reviewElement.innerHTML = reviewHTML;
            reviewsListDiv.appendChild(reviewElement);
        });
    } catch (error) {
        reviewsListDiv.innerHTML = `<p style="color: red;">Não foi possível carregar as avaliações.</p>`;
    }
}

/**
 * Verifica se o cliente já avaliou o restaurante.
 */
async function checkIfUserHasReviewed(idRestaurante, idCliente) {
    const btnAvaliar = document.getElementById("btn-avaliar");
    if (!btnAvaliar) return;
    try {
        const response = await fetch(`${API_BASE_URL}/avaliacoes/check?idRestaurante=${idRestaurante}&idCliente=${idCliente}`);
        if (!response.ok) return;
        const data = await response.json();
        if (data.jaAvaliou) {
            btnAvaliar.disabled = true;
            btnAvaliar.textContent = "Você já avaliou";
            btnAvaliar.onclick = null;
        } else {
            btnAvaliar.disabled = false;
            btnAvaliar.textContent = "Avaliar";
            btnAvaliar.onclick = openReviewModal;
        }
    } catch (error) {
        console.error("Erro ao checar avaliação:", error);
    }
}

/**
 * Configura os botões de ação ("Reservar", "Avaliar").
 */
function setupActionButtons() {
    const usuarioLogado = localStorage.getItem("usuarioLogado");
    const restauranteLogado = localStorage.getItem("restauranteLogado");
    const btnRealizarReserva = document.getElementById("btn-realizar-reserva");
    const btnAvaliar = document.getElementById("btn-avaliar");

    if (btnRealizarReserva && btnAvaliar) {
        if (usuarioLogado && !restauranteLogado) {
            btnRealizarReserva.disabled = false;
            btnAvaliar.disabled = false;
            btnRealizarReserva.onclick = () => location.href = `../Reserva/ReservaDados.html?idRestaurante=${currentRestaurantId}`;
            const idCliente = JSON.parse(usuarioLogado).idCliente;
            checkIfUserHasReviewed(currentRestaurantId, idCliente);
        } else {
            btnRealizarReserva.disabled = true;
            btnAvaliar.disabled = true;
            btnAvaliar.textContent = "Faça login para avaliar";
            btnAvaliar.onclick = null;
        }
    }
}

/**
 * Busca os detalhes do restaurante.
 */
async function fetchRestaurantDetails(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/restaurante/${id}`);
        if (!response.ok) throw new Error(`Falha ao carregar detalhes`);
        const data = await response.json();
        populateRestaurantInfo(data);
        populateCarouselImages(data.imagensGaleria || []);
        if (data.nomeRestaurante) document.title = data.nomeRestaurante;
    } catch (error) {
        console.error('Erro ao buscar detalhes:', error);
        document.querySelector('.restaurant-info').innerHTML = '<p>Não foi possível carregar os detalhes do restaurante.</p>';
    }
}

/**
 * Busca os horários de funcionamento.
 */
async function fetchRestaurantHours(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/horarios/restaurante/${id}`);
        if (!response.ok && response.status !== 204) throw new Error(`Falha ao carregar horários`);
        
        const hoursData = response.status === 204 ? [] : await response.json();
        const infoDiv = document.querySelector('.restaurant-info');
        
        let hoursContainer = document.getElementById('horarios-container');
        if (!hoursContainer && infoDiv) {
             hoursContainer = document.createElement('div');
             hoursContainer.id = 'horarios-container';
             const buttonsDiv = infoDiv.querySelector('.buttons');
             infoDiv.insertBefore(hoursContainer, buttonsDiv);
        }

        if (hoursContainer) {
            hoursContainer.innerHTML = '<p><strong>Horários de funcionamento:</strong></p>';
            
            const diasAbertos = hoursData ? hoursData.filter(h => h.ativo) : [];
            
            if (diasAbertos.length === 0) {
                 hoursContainer.innerHTML += '<p>Fechado ou horários não informados.</p>';
            } else {
                 const dayOrder = ["Domingo", "Segunda", "Terca", "Quarta", "Quinta", "Sexta", "Sabado"];
                 diasAbertos.sort((a, b) => dayOrder.indexOf(a.diaSemana) - dayOrder.indexOf(b.diaSemana));
                 diasAbertos.forEach(h => {
                     const p = document.createElement('p');
                     p.textContent = `${h.diaSemana}: ${h.horaInicio} - ${h.horaFim}`;
                     hoursContainer.appendChild(p);
                 });
            }
        }
    } catch (error) {
        console.error('Erro ao buscar horários:', error);
    }
}


// ========================================================
// EVENT LISTENER PRINCIPAL (EXECUTADO NO FINAL)
// ========================================================
document.addEventListener('DOMContentLoaded', () => {
    // Inicializa referências a elementos do DOM que podem ser usados por várias funções
    reviewModal = document.getElementById('reviewModal');
    reviewForm = document.getElementById('reviewForm');
    closeReviewModalButton = document.getElementById('closeReviewModalBtn');
    cancelReviewButton = document.getElementById('cancelReviewBtn');

    // Pega o ID do restaurante da URL
    const params = new URLSearchParams(window.location.search);
    currentRestaurantId = params.get('id');

    if (!currentRestaurantId) {
        document.querySelector('main').innerHTML = '<p style="text-align:center; color:red;">Erro: ID do restaurante não fornecido na URL.</p>';
        return;
    }

    // Inicializa a página buscando todos os dados
    fetchRestaurantDetails(currentRestaurantId);
    fetchRestaurantHours(currentRestaurantId);
    fetchAndDisplayReviews(currentRestaurantId);
    setupActionButtons();

    // Configura os listeners para os modais e botões do carrossel
    if (reviewForm) reviewForm.addEventListener('submit', handleReviewSubmit);
    if (closeReviewModalButton) closeReviewModalButton.onclick = closeReviewModal;
    if (cancelReviewButton) cancelReviewButton.onclick = closeReviewModal;
    
    window.onclick = (event) => {
        if (reviewModal && event.target == reviewModal) closeReviewModal();
    };
    
    const prevButton = document.querySelector('.carousel .prev');
    if (prevButton) prevButton.onclick = () => moveSlide(-1);
    
    const nextButton = document.querySelector('.carousel .next');
    if (nextButton) nextButton.onclick = () => moveSlide(1);
});
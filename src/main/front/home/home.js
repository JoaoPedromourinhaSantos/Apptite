const API_BASE_URL = 'http://localhost:8080';
let favoritosDoUsuario = new Set(); // Armazena os IDs dos restaurantes favoritados

const abrirFiltrosBtn = document.getElementById('abrirFiltrosBtn');
const popupFiltros = document.getElementById('popupFiltros');
const fecharPopupFiltrosBtn = document.getElementById('fecharPopupFiltrosBtn');
const aplicarFiltrosBtn = document.getElementById('aplicarFiltrosBtn');
const limparFiltrosBtn = document.getElementById('limparFiltrosBtn');
const listaFiltrosPopup = document.getElementById('listaFiltrosPopup');


/**
 * Verifica o status do login e exibe/oculta os botões de ação do cabeçalho.
 */
function checkLoginStatusAndToggleButtons() {
    const usuarioLogado = localStorage.getItem("usuarioLogado");
    const restauranteLogado = localStorage.getItem("restauranteLogado");
    const buttonsContainerDiv = document.querySelector('.intro .buttons');

    if (buttonsContainerDiv) {
        buttonsContainerDiv.style.display = (usuarioLogado || restauranteLogado) ? 'none' : 'block';
    }
}


async function populateFiltersPopup() {
    try {
        const response = await fetch(`${API_BASE_URL}/filtros`);
        if (!response.ok) throw new Error("Não foi possível carregar os filtros.");
        
        const allFilters = await response.json();
        listaFiltrosPopup.innerHTML = ""; // Limpa o conteúdo

        allFilters.forEach(filtro => {
            const itemDiv = document.createElement('div');
            itemDiv.className = 'filtro-item'; // Use para estilização
            itemDiv.innerHTML = `
                <input type="checkbox" id="filtro-home-${filtro.idFiltro}" value="${filtro.idFiltro}">
                <label for="filtro-home-${filtro.idFiltro}">${filtro.nomeFiltro}</label>
            `;
            listaFiltrosPopup.appendChild(itemDiv);
        });
    } catch (error) {
        console.error("Erro ao popular filtros:", error);
        listaFiltrosPopup.innerHTML = "<p>Erro ao carregar filtros.</p>";
    }
}

/**
 * Busca restaurantes com base nos filtros selecionados.
 */
async function applyFilters() {
    const selectedCheckboxes = listaFiltrosPopup.querySelectorAll('input[type="checkbox"]:checked');
    const selectedIds = Array.from(selectedCheckboxes).map(cb => cb.value);

    // Constrói a URL com os parâmetros de consulta
    const queryParams = new URLSearchParams();
    selectedIds.forEach(id => queryParams.append('ids', id));
    
    // A URL final será /buscar-por-todos-filtros?ids=1&ids=5...
    const url = `${API_BASE_URL}/restaurante/buscar-por-todos-filtros?${queryParams.toString()}`;

    try {
        const response = await fetch(url);
        if (!response.ok && response.status !== 204) throw new Error('Falha ao buscar restaurantes filtrados.');
        
        const restaurantesFiltrados = response.status === 204 ? [] : await response.json();
        displayRestaurants(restaurantesFiltrados); // Reutiliza a função que exibe os cards
        
        popupFiltros.style.display = 'none'; // Fecha o popup após aplicar
    } catch (error) {
        console.error("Erro ao aplicar filtros:", error);
        alert("Não foi possível aplicar os filtros.");
    }
}

/**
 * Limpa os filtros selecionados e busca todos os restaurantes novamente.
 */
function clearFilters() {
    // Desmarca todos os checkboxes
    listaFiltrosPopup.querySelectorAll('input[type="checkbox"]:checked').forEach(cb => cb.checked = false);
    // Busca todos os restaurantes novamente
    fetchRestaurants();
    popupFiltros.style.display = 'none';
}


/**
 * Busca os restaurantes favoritos do usuário logado e armazena os IDs.
 */
async function fetchUserFavorites() {
    const usuarioLogado = JSON.parse(localStorage.getItem("usuarioLogado"));
    if (usuarioLogado && usuarioLogado.idCliente) {
        try {
            const response = await fetch(`${API_BASE_URL}/favoritos/cliente/${usuarioLogado.idCliente}`);
            if (response.ok) {
                const favoritos = await response.json();
                const idsFavoritos = favoritos.map(rest => rest.idRestaurante);
                favoritosDoUsuario = new Set(idsFavoritos);
            }
        } catch (error) {
            console.error("Erro ao buscar favoritos do usuário:", error);
        }
    }
}

/**
 * Adiciona ou remove um restaurante dos favoritos ao clicar no ícone de coração.
 */
async function toggleFavorite(event, idRestaurante) {
    event.stopPropagation();
    event.preventDefault();
    
    const usuarioLogado = JSON.parse(localStorage.getItem("usuarioLogado"));
    if (!usuarioLogado || !usuarioLogado.idCliente) {
        alert("Você precisa estar logado como cliente para favoritar um restaurante.");
        window.location.href = "../Login/login.html";
        return;
    }

    const favoritoRequest = {
        idCliente: usuarioLogado.idCliente,
        idRestaurante: idRestaurante
    };

    try {
        const response = await fetch(`${API_BASE_URL}/favoritos/alternar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(favoritoRequest)
        });

        if (!response.ok) throw new Error('Falha ao atualizar favorito.');
        
        const data = await response.json();
        const heartIcon = event.target;
        
        if (data.isFavorito) {
            heartIcon.classList.add('active');
            heartIcon.textContent = '♥';
            favoritosDoUsuario.add(idRestaurante);
        } else {
            heartIcon.classList.remove('active');
            heartIcon.textContent = '♡';
            favoritosDoUsuario.delete(idRestaurante);
        }
    } catch (error) {
        console.error("Erro ao favoritar:", error);
        alert("Não foi possível atualizar o favorito. Tente novamente.");
    }
}

/**
 * Busca todos os restaurantes da API.
 */
async function fetchRestaurants() {
    const apiUrl = `${API_BASE_URL}/restaurante/todos`;
    try {
        const response = await fetch(apiUrl);
        if (!response.ok && response.status !== 204) throw new Error(`HTTP error! status: ${response.status}`);
        
        const restaurants = response.status === 204 ? [] : await response.json();
        displayRestaurants(restaurants);
    } catch (error) {
        console.error("Não foi possível buscar os restaurantes:", error);
        const container = document.querySelector('.card-container');
        if (container) container.innerHTML = '<p>Erro ao carregar os restaurantes.</p>';
    }
}

/**
 * Exibe os cards de restaurantes na página.
 * @param {Array} restaurants - A lista de objetos de restaurantes.
 */
function displayRestaurants(restaurants) {
    const container = document.querySelector('.card-container');
    if (!container) return;
    container.innerHTML = '';

    if (!restaurants || restaurants.length === 0) {
        container.innerHTML = '<p>Nenhum restaurante disponível no momento.</p>';
        return;
    }

    // << INÍCIO DA CORREÇÃO: Ordena a lista de restaurantes >>
    restaurants.sort((a, b) => {
        const aIsFavorite = favoritosDoUsuario.has(a.idRestaurante);
        const bIsFavorite = favoritosDoUsuario.has(b.idRestaurante);

        if (aIsFavorite && !bIsFavorite) {
            return -1; // 'a' (favorito) vem antes de 'b'
        }
        if (!aIsFavorite && bIsFavorite) {
            return 1; // 'b' (favorito) vem antes de 'a'
        }
        return 0; // Mantém a ordem original entre dois favoritos ou dois não favoritos
    });
    // << FIM DA CORREÇÃO >>

    restaurants.forEach(restaurant => {
        const card = document.createElement('div');
        card.classList.add('card');

        const displayStars = (rating) => {
            let stars = '';
            const numericRating = Number(rating) || 0;
            for (let i = 1; i <= 5; i++) stars += i <= numericRating ? '★' : '☆';
            return `<span style="color: #FFD700;">${stars}</span>`;
        };

        const address = restaurant.ruaEndereco ? `${restaurant.ruaEndereco}, ${restaurant.numeroEndereco || 's/n'}` : 'Endereço não disponível';
        const profileLink = `../Exibir Perfil Restaurante/perfil_restaurante.html?id=${restaurant.idRestaurante}`;
        const formattedPrice = restaurant.faixaPreco || 'Não informada';
        
        let imageUrl = 'download.jpg'; 
        let imageAlt = restaurant.nomeRestaurante || 'Restaurante';

        if (restaurant.imagensGaleria && restaurant.imagensGaleria.length > 0) {
            const sortedGallery = [...restaurant.imagensGaleria].sort((a, b) => (a.ordem || 0) - (b.ordem || 0));
            if (sortedGallery[0] && sortedGallery[0].id) {
                imageUrl = `${API_BASE_URL}/restaurante/galeria/imagem/${sortedGallery[0].id}`;
                imageAlt = sortedGallery[0].nomeOriginalImagem || imageAlt;
            }
        } else if (restaurant.fotoCardapio) {
            imageUrl = `${API_BASE_URL}/${restaurant.fotoCardapio}`;
        }
        
        const isFavorito = favoritosDoUsuario.has(restaurant.idRestaurante);
        const heartIcon = isFavorito ? '♥' : '♡';
        const activeClass = isFavorito ? 'active' : '';

        card.innerHTML = `
            <img src="${imageUrl}" alt="${imageAlt}" onerror="this.onerror=null;this.src='download.jpg';">
            <div class="card-content">
                <h3>${restaurant.nomeRestaurante || 'Restaurante'}</h3>
                <p><strong>Faixa de preço:</strong> ${formattedPrice}</p>
                <p><strong>Endereço:</strong> ${address}</p>
                <div class="card-footer">
                    <span class="stars">${displayStars(restaurant.avaliacaoGeral)}</span>
                    <span class="favorite ${activeClass}" data-id-restaurante="${restaurant.idRestaurante}">${heartIcon}</span>
                </div>
                <a href="${profileLink}" class="card-button">Visitar</a>
            </div>
        `;
        container.appendChild(card);
    });

    // Adiciona o listener de clique para todos os botões de favorito após criá-los
    container.querySelectorAll('.favorite').forEach(heart => {
        const idRestaurante = parseInt(heart.dataset.idRestaurante, 10);
        heart.addEventListener('click', (event) => toggleFavorite(event, idRestaurante));
    });
}

/**
 * Função principal para inicializar a página.
 */
async function initializeHomepage() {
    checkLoginStatusAndToggleButtons();
    await fetchUserFavorites();
    fetchRestaurants(); // Carrega todos os restaurantes inicialmente
    populateFiltersPopup(); // Prepara o popup de filtros

    // Adiciona os Event Listeners para o popup de filtros
    abrirFiltrosBtn.addEventListener('click', () => popupFiltros.style.display = 'flex');
    fecharPopupFiltrosBtn.addEventListener('click', () => popupFiltros.style.display = 'none');
    aplicarFiltrosBtn.addEventListener('click', applyFilters);
    limparFiltrosBtn.addEventListener('click', clearFilters);
}

// Event listener principal
document.addEventListener('DOMContentLoaded', initializeHomepage);
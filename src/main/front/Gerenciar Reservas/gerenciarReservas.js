document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080';
    const restaurantNameH1 = document.getElementById('establishmentName');
    const voltarPerfilLink = document.getElementById('voltarPerfilEstabelecimento');

    // Referências para os containers de cada aba
    const listaReservasSolicitadasDiv = document.getElementById('listaReservasSolicitadas');
    const listaReservasAgendadasDiv = document.getElementById('listaReservasAgendadas');
    const listaReservasRecusadasDiv = document.getElementById('listaReservasRecusadas');
    const listaReservasPassadasDiv = document.getElementById('listaReservasPassadas');
    const listaReservasFinalizadasDiv = document.getElementById('listaReservasFinalizadas'); // Container para a nova aba

    const tabs = document.querySelectorAll('.tab-button');
    const tabContents = document.querySelectorAll('.tab-content');

    const params = new URLSearchParams(window.location.search);
    const idRestaurante = params.get('idRestaurante');

    if (!idRestaurante) {
        alert('ID do restaurante não fornecido na URL!');
        return;
    }

    if (voltarPerfilLink) {
        voltarPerfilLink.href = `../Perfil - Estabelecimento/perfil_estabelecimento.html?id=${idRestaurante}`;
    }
    
    fetch(`${API_BASE_URL}/restaurante/${idRestaurante}`)
        .then(response => response.ok ? response.json() : Promise.reject('Restaurante não encontrado.'))
        .then(data => {
            if (restaurantNameH1) restaurantNameH1.textContent = `Gerenciar Reservas - ${data.nomeRestaurante || 'Restaurante'}`;
        });

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            const targetTabContentId = `tab-${tab.dataset.tab}`;
            tabContents.forEach(tc => {
                tc.classList.remove('active');
                if (tc.id === targetTabContentId) tc.classList.add('active');
            });
        });
    });

    function formatDate(dateString) {
        if (!dateString) return 'N/A';
        const parts = dateString.split('-');
        return parts.length === 3 ? `${parts[2]}/${parts[1]}/${parts[0]}` : dateString;
    }

    function formatTime(timeString) {
        if (!timeString) return 'N/A';
        const parts = timeString.split(':');
        return `${parts[0]}:${parts[1]}`;
    }

    function criarCardReserva(reserva, statusTab) {
        const card = document.createElement('div');
        card.classList.add('reserva-card');
        card.dataset.idReserva = reserva.idReserva;

        let nomeCliente = reserva.nomeCliente || `Cliente (ID: ${reserva.idCliente})`;

        card.innerHTML = `
            <h3>Reserva #${reserva.idReserva}</h3>
            <p><strong>Cliente:</strong> ${nomeCliente}</p>
            <p><strong>Data:</strong> ${formatDate(reserva.dataReserva)}</p>
            <p><strong>Horário:</strong> ${formatTime(reserva.horarioChegada)}</p>
            <p><strong>Pessoas:</strong> ${reserva.quantidadePessoas}</p>
            <p><strong>Status Atual:</strong> ${reserva.statusReserva}</p>
            <div class="reserva-actions"></div>
        `;

        const actionsDiv = card.querySelector('.reserva-actions');

        if (statusTab === 'solicitadas') {
            const btnAceitar = document.createElement('button');
            btnAceitar.className = 'btn-aceitar';
            btnAceitar.textContent = 'Aceitar';
            btnAceitar.onclick = () => handleAcaoReserva(reserva.idReserva, 'aceitar');
            actionsDiv.appendChild(btnAceitar);

            const btnRecusar = document.createElement('button');
            btnRecusar.className = 'btn-recusar';
            btnRecusar.textContent = 'Recusar';
            btnRecusar.onclick = () => handleAcaoReserva(reserva.idReserva, 'recusar');
            actionsDiv.appendChild(btnRecusar);
        } else if (statusTab === 'agendadas') {
            const btnCancelar = document.createElement('button');
            btnCancelar.className = 'btn-cancelar-est';
            btnCancelar.textContent = 'Cancelar Reserva';
            btnCancelar.onclick = () => handleCancelarReserva(reserva.idReserva);
            actionsDiv.appendChild(btnCancelar);
        } else if (statusTab === 'passadas') {
            const btnCompareceu = document.createElement('button');
            btnCompareceu.className = 'btn-aceitar'; // Reutiliza estilo verde
            btnCompareceu.textContent = 'Cliente Compareceu';
            btnCompareceu.onclick = () => handleCompareceu(reserva.idReserva);
            actionsDiv.appendChild(btnCompareceu);
        }
        return card;
    }

    async function fetchReservas(pathSegment, containerDiv) {
        if(!containerDiv) return;
        containerDiv.innerHTML = `<p class="loading">Carregando...</p>`;
        try {
            const response = await fetch(`${API_BASE_URL}/reservas/restaurante/${idRestaurante}/${pathSegment}`);
            
            if (response.status === 204) {
                 containerDiv.innerHTML = `<p class="no-reservas">Nenhuma reserva encontrada.</p>`;
                 return;
            }
            if (!response.ok) throw new Error(await response.text());
            
            const reservas = await response.json();
            containerDiv.innerHTML = '';
            reservas.forEach(reserva => {
                containerDiv.appendChild(criarCardReserva(reserva, pathSegment));
            });
        } catch (error) {
            console.error(`Erro ao buscar reservas ${pathSegment}:`, error);
            containerDiv.innerHTML = `<p style="color:red;">Erro ao carregar reservas.</p>`;
        }
    }

    async function fetchTaxaComparecimento(idEstabelecimento) {
        const elementoTaxa = document.getElementById("taxaComparecimentoValor");
        if (!elementoTaxa) return;

        elementoTaxa.textContent = "Carregando...";
        elementoTaxa.style.color = '#777';

        try {
            // ATENÇÃO: Se o seu back-end usa autenticação, você precisará enviar o token aqui.
            const response = await fetch(`${API_BASE_URL}/reservas/indicadores/taxa-comparecimento/${idEstabelecimento}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    // Exemplo de como enviar um token do localStorage (se você usar JWT)
                    // "Authorization": `Bearer ${localStorage.getItem('seuTokenAqui')}` 
                }
            });

            if (response.ok) {
                const taxa = await response.json();
                elementoTaxa.textContent = `${taxa.toFixed(2)}%`;

                // Estilização condicional
                if (taxa >= 80) {
                    elementoTaxa.style.color = '#28a745'; // Verde
                } else if (taxa >= 50) {
                    elementoTaxa.style.color = '#ffc107'; // Amarelo
                } else {
                    elementoTaxa.style.color = '#dc3545'; // Vermelho
                }
            } else {
                const errorText = await response.text();
                elementoTaxa.textContent = `Erro: ${response.status} - ${errorText}`;
                elementoTaxa.style.color = 'red';
                console.error("Erro ao carregar taxa de comparecimento:", response.status, errorText);
            }

        } catch (erro) {
            elementoTaxa.textContent = "Erro de conexão com o servidor.";
            elementoTaxa.style.color = 'red';
            console.error("Erro de rede ao buscar taxa de comparecimento:", erro);
        }
    }

    async function fetchTaxaComparecimento(idRestaurante) {
        const elementoTaxa = document.getElementById("taxaComparecimentoValor");
        if (!elementoTaxa) return;

        elementoTaxa.textContent = "Carregando...";
        elementoTaxa.style.color = '#777';

        try {
            const response = await fetch(`${API_BASE_URL}/reservas/indicadores/taxa-comparecimento/${idRestaurante}`);

            if (response.ok) {
                const data = await response.json(); // Espera um objeto como { taxa: 85.5 }
                const taxa = data.taxa;
                elementoTaxa.textContent = `${taxa.toFixed(2)}%`; // Formata para 2 casas decimais

                // Estilização condicional para dar feedback visual
                if (taxa >= 80) {
                    elementoTaxa.style.color = '#28a745'; // Verde para bom
                } else if (taxa >= 50) {
                    elementoTaxa.style.color = '#ffc107'; // Amarelo para médio
                } else {
                    elementoTaxa.style.color = '#dc3545'; // Vermelho para ruim
                }
            } else {
                elementoTaxa.textContent = "Erro ao carregar";
                elementoTaxa.style.color = 'red';
            }
        } catch (erro) {
            elementoTaxa.textContent = "Erro de conexão.";
            elementoTaxa.style.color = 'red';
            console.error("Erro de rede ao buscar taxa de comparecimento:", erro);
        }
    }

    async function handleAcaoReserva(idReserva, acao) {
        if (!confirm(`Tem certeza que deseja ${acao.toUpperCase()} a reserva #${idReserva}?`)) return;
        try {
            const response = await fetch(`${API_BASE_URL}/reservas/${idReserva}/${acao}?idRestauranteLogado=${idRestaurante}`, {
                method: 'PUT'
            });
            if (!response.ok) throw new Error(await response.text());
            alert(`Reserva ${acao} com sucesso!`);
            fetchAllReservas();
        } catch (error) {
            alert(`Erro ao ${acao} reserva: ${error.message}`);
        }
    }
    
    const handleAceitarReserva = (id) => handleAcaoReserva(id, 'aceitar');
    const handleRecusarReserva = (id) => handleAcaoReserva(id, 'recusar');

    async function handleCancelarReserva(idReserva) {
        if (!confirm(`Tem certeza que deseja CANCELAR a reserva agendada #${idReserva}?`)) return;
        try {
            const response = await fetch(`${API_BASE_URL}/reservas/${idReserva}/cancelar-pelo-restaurante?idRestauranteLogado=${idReserva}`, {
                method: 'PUT'
            });
            if (!response.ok) throw new Error(await response.text());
            alert('Reserva cancelada com sucesso!');
            fetchAllReservas();
        } catch (error) {
            alert(`Erro ao cancelar reserva: ${error.message}`);
        }
    }

    async function handleCompareceu(idReserva) {
        if (!confirm(`Confirmar que o cliente da reserva #${idReserva} compareceu?`)) return;
        try {
            const response = await fetch(`${API_BASE_URL}/reservas/${idReserva}/compareceu?idRestauranteLogado=${idRestaurante}`, {
                method: 'PUT'
            });
            if (!response.ok) throw new Error(await response.text());
            alert('Reserva marcada como "comparecida" com sucesso!');
            fetchAllReservas();
        } catch (error) {
            alert(`Erro ao confirmar comparecimento: ${error.message}`);
        }
    }

    function fetchAllReservas() {
        fetchReservas('solicitadas', listaReservasSolicitadasDiv);
        fetchReservas('agendadas', listaReservasAgendadasDiv);
        fetchReservas('recusadas', listaReservasRecusadasDiv);
        fetchReservas('passadas', listaReservasPassadasDiv);
        fetchReservas('finalizadas', listaReservasFinalizadasDiv);
    }

    // Inicializa a página
    if (idRestaurante) {
        fetchAllReservas();
        fetchTaxaComparecimento(idRestaurante); // << CHAMA A NOVA FUNÇÃO AQUI
    }
});
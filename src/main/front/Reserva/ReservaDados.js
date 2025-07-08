document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080';
    const formSolicitarReserva = document.getElementById('formSolicitarReserva');
    const dataReservaInput = document.getElementById('dataReserva');
    const horarioChegadaInput = document.getElementById('horarioChegada');
    const quantidadePessoasInput = document.getElementById('quantidadePessoas');
    const restaurantNameH2 = document.getElementById('restaurantName');
    const voltarLink = document.getElementById('voltarPerfilRestaurante');

    const params = new URLSearchParams(window.location.search);
    // << CORREÇÃO 1: Buscar o parâmetro com o nome novo "idRestaurante" >>
    const idRestaurante = params.get('idRestaurante');

    if (!idRestaurante) {
        alert('ID do restaurante não fornecido na URL!');
        restaurantNameH2.textContent = 'Erro: Restaurante não especificado.';
        if(formSolicitarReserva) formSolicitarReserva.style.display = 'none';
        if(voltarLink) voltarLink.style.display = 'none';
        return;
    }

    // Configura o link de "Voltar" para usar o ID correto
    if (voltarLink) {
        voltarLink.href = `../Exibir Perfil Restaurante/perfil_restaurante.html?id=${idRestaurante}`;
    }

    // << CORREÇÃO 2: Buscar e exibir nome do restaurante usando o endpoint e a propriedade corretos >>
    fetch(`${API_BASE_URL}/restaurante/${idRestaurante}`)
        .then(response => {
            if (!response.ok) throw new Error('Restaurante não encontrado.');
            return response.json();
        })
        .then(restaurante => {
            restaurantNameH2.textContent = `Reserva em: ${restaurante.nomeRestaurante || 'Restaurante Desconhecido'}`;
        })
        .catch(error => {
            console.error('Erro ao buscar dados do restaurante:', error);
            restaurantNameH2.textContent = 'Não foi possível carregar dados do restaurante.';
            if(formSolicitarReserva) formSolicitarReserva.style.display = 'none';
        });

    // Configurar o campo de data (lógica mantida, está boa)
    const hoje = new Date();
    const dataMinima = new Date(hoje);
    dataMinima.setDate(hoje.getDate() + 1); // A reserva só pode ser a partir de amanhã

    const dataMaxima = new Date(hoje);
    dataMaxima.setDate(hoje.getDate() + 14);

    if (dataReservaInput) {
        dataReservaInput.min = dataMinima.toISOString().split('T')[0];
        dataReservaInput.max = dataMaxima.toISOString().split('T')[0];
    }

    if (formSolicitarReserva) {
        formSolicitarReserva.addEventListener('submit', async (event) => {
            event.preventDefault();

            // << CORREÇÃO 3: Usar a propriedade 'idCliente' para pegar o ID do usuário logado >>
            const usuarioLogado = JSON.parse(localStorage.getItem("usuarioLogado"));
            if (!usuarioLogado || !usuarioLogado.idCliente) {
                alert("Você precisa estar logado como cliente para solicitar uma reserva.");
                return;
            }
            const idCliente = usuarioLogado.idCliente;

            const dataReserva = dataReservaInput.value;
            const horarioChegada = horarioChegadaInput.value;
            const quantidadePessoas = parseInt(quantidadePessoasInput.value, 10);

            // Validações no frontend (mantidas, estão boas)
            if (!dataReserva || !horarioChegada || !quantidadePessoas || quantidadePessoas <= 0) {
                alert('Por favor, preencha todos os campos corretamente.');
                return;
            }

            const reservaPayload = {
                dataReserva: dataReserva,
                horarioChegada: horarioChegada,
                quantidadePessoas: quantidadePessoas
            };

            console.log("Enviando solicitação de reserva:", reservaPayload);

            try {
                // << CORREÇÃO 4: Usar os nomes de parâmetros corretos na URL >>
                const response = await fetch(`${API_BASE_URL}/reservas/solicitar?idCliente=${idCliente}&idRestaurante=${idRestaurante}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(reservaPayload)
                });

                if (response.ok) {
                    alert('Solicitação de reserva enviada com sucesso! Aguarde a confirmação do estabelecimento.');
                    formSolicitarReserva.reset();
                    // << CORREÇÃO 5: Usar o ID correto para redirecionar de volta >>
                    window.location.href = `../Exibir Perfil Restaurante/perfil_restaurante.html?id=${idRestaurante}`;
                } else {
                    const errorText = await response.text();
                    alert(`Erro ao enviar solicitação: ${errorText || response.status}`);
                }
            } catch (error) {
                console.error('Erro na API de solicitação de reserva:', error);
                alert('Ocorreu um erro de comunicação ao tentar enviar sua solicitação.');
            }
        });
    }
});
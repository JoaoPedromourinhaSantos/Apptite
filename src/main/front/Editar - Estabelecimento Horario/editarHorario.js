document.addEventListener("DOMContentLoaded", () => {
    const dias = ["Domingo", "Segunda", "Terca", "Quarta", "Quinta", "Sexta", "Sabado"];
    const diasContainer = document.querySelector(".dias-container");
    const confirmarBtn = document.getElementById("confirmarBtn");

    // << CORREÇÃO 1: Usar a chave e a propriedade corretas do localStorage >>
    const restauranteLogado = JSON.parse(localStorage.getItem("restauranteLogado"));
    const idRestaurante = restauranteLogado?.idRestaurante;
    
    let horariosAtuais = []; // Armazena os horários que vêm do banco

    if (!idRestaurante) {
        alert("Erro: Restaurante não logado ou ID inválido. Faça o login novamente.");
        window.location.href = "../Login/login.html";
        return;
    }

    // << CORREÇÃO 2: Chamar o endpoint correto para buscar os horários >>
    fetch(`http://localhost:8080/horarios/restaurante/${idRestaurante}`)
        .then(res => {
            if (res.ok) {
                return res.json();
            }
            if (res.status === 204) { // No Content
                return []; // Retorna um array vazio se não houver horários cadastrados
            }
            throw new Error('Falha ao carregar horários.');
        })
        .then(data => {
            horariosAtuais = data;
            gerarCamposDias();
        })
        .catch(err => {
            console.error("Erro ao carregar horários:", err);
            gerarCamposDias(); // Gera os campos mesmo se falhar, para o usuário poder criar do zero
        });

    function gerarCamposDias() {
        diasContainer.innerHTML = "";

        dias.forEach(dia => {
            // Usa o array 'horariosAtuais' para encontrar os dados do dia
            const horarioDoDia = horariosAtuais.find(h => h.diaSemana === dia);
            
            const id = horarioDoDia?.idHorario || null;
            const ativo = horarioDoDia?.ativo ?? false;
            const abertura = horarioDoDia?.horaInicio || "08:00";
            const fechamento = horarioDoDia?.horaFim || "18:00";

            diasContainer.innerHTML += `
                <div class="dia-linha" data-id-horario="${id || ''}">
                    <label for="${dia}-ativo">
                        <input type="checkbox" id="${dia}-ativo" ${ativo ? "checked" : ""}>
                        <span class="toggle-switch"></span>
                        ${dia}
                    </label>
                    <div class="dia-horarios">
                        <input type="time" id="${dia}-abertura" value="${abertura}" ${!ativo ? "disabled" : ""}>
                        <span>às</span>
                        <input type="time" id="${dia}-fechamento" value="${fechamento}" ${!ativo ? "disabled" : ""}>
                    </div>
                </div>
            `;
        });

        // Adiciona os listeners para os checkboxes habilitarem/desabilitarem os inputs de hora
        diasContainer.querySelectorAll(".dia-linha").forEach(linha => {
            const checkbox = linha.querySelector("input[type='checkbox']");
            const inputsTime = linha.querySelectorAll("input[type='time']");
            checkbox.addEventListener("change", () => {
                inputsTime.forEach(input => input.disabled = !checkbox.checked);
            });
        });
    }

    confirmarBtn.addEventListener("click", async (e) => {
        e.preventDefault();

        // << CORREÇÃO 3: Montar o corpo da requisição no formato do DTO >>
        const payload = [];
        diasContainer.querySelectorAll(".dia-linha").forEach(linha => {
            const dia = linha.querySelector("label").textContent.trim();
            const idHorario = linha.dataset.idHorario ? parseInt(linha.dataset.idHorario) : null;
            const ativo = linha.querySelector("input[type='checkbox']").checked;
            const horaInicio = linha.querySelector(`#${dia}-abertura`).value;
            const horaFim = linha.querySelector(`#${dia}-fechamento`).value;
            
            // Adiciona ao payload para enviar ao backend
            payload.push({
                idHorario: idHorario, // Envia o ID se for um horário existente
                diaSemana: dia,
                horaInicio: ativo ? horaInicio : "00:00",
                horaFim: ativo ? horaFim : "00:00",
                ativo: ativo
                // Não precisa enviar o objeto 'restaurante', o ID já está na URL
            });
        });

        try {
            // << CORREÇÃO 4: Chamar o endpoint correto para atualizar >>
            const response = await fetch(`http://localhost:8080/horarios/restaurante/${idRestaurante}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            if (!response.ok) throw new Error(await response.text());

            alert("Horários atualizados com sucesso!");
            // << CORREÇÃO 5: Corrigir typo no nome do arquivo de perfil >>
            window.location.href = "../Perfil - Estabelecimento/perfil_estabelecimento.html"; 

        } catch (err) {
            console.error("Erro ao salvar horários:", err);
            alert("Erro ao salvar horários: " + err.message);
        }
    });
});
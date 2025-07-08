const distanciaSlider = document.getElementById('distancia');
const distanciaValor = document.getElementById('distanciaValor');
const confirmBtn = document.querySelector('.confirm-btn');
const resetBtn = document.querySelector('.reset-btn');
const resultado = document.getElementById('resultado');

distanciaSlider.addEventListener('input', () => {
  distanciaValor.textContent = distanciaSlider.value;
});

confirmBtn.addEventListener('click', () => {
  const filtros = {
    culinaria: getCheckedValues('culinaria'),
    valor: getCheckedValues('valor'),
    avaliacao: getCheckedValues('avaliacao'),
    distancia: `${distanciaSlider.value} km`
  };

  resultado.innerHTML = `
    <h3>Filtros aplicados:</h3>
    <p><strong>Culinária:</strong> ${filtros.culinaria.join(', ') || 'Nenhum'}</p>
    <p><strong>Valor:</strong> ${filtros.valor.join(', ') || 'Nenhum'}</p>
    <p><strong>Avaliação:</strong> ${filtros.avaliacao.join(', ') || 'Nenhum'}</p>
    <p><strong>Distância:</strong> ${filtros.distancia}</p>
  `;
  resultado.style.display = 'block';
});

resetBtn.addEventListener('click', () => {
  document.querySelectorAll('input[type="checkbox"]').forEach(cb => cb.checked = false);
  distanciaSlider.value = 2;
  distanciaValor.textContent = 2;
  resultado.style.display = 'none';
});

function getCheckedValues(name) {
  return Array.from(document.querySelectorAll(`input[name="${name}"]:checked`)).map(cb => cb.value);
}

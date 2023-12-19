function manterEscolha(radio) {
  // Impedir desmarcar a opção selecionada
  if (radio.checked) {
    radio.onclick = function() { return false; };
  }
}
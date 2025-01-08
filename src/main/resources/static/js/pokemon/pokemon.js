$(() => {
   /* 포켓몬 타입 아이콘 추가 */
   setTypeIcon();
});

/**
 * 포켓몬 타입 아이콘 추가
 */
function setTypeIcon() {
   const pokemonList = $('.row.gutters').children('.pokemonCard');

   pokemonList.each((_, pokemonCard) => {
      const iconWrapp = $(pokemonCard).find('.iconWrapp');
      const types = $(pokemonCard).find('.types');

      const innerHtml = types.map((index, typeElement) => {
         const type = $(typeElement).val();
         const typeCount = types.length;
         const iconClass = index === 0 && typeCount > 1 ? `icon first ${type}` : `icon ${type}`;
         return `
            <div class="${iconClass}" title="${type}">
                <img src="/svg/${type}.svg">
            </div>`;
      }).get().join('');

      iconWrapp.append(innerHtml);
   });
}
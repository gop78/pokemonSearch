$(() => {
   /* 포켓몬 타입 아이콘 추가 */
   setTypeIcon();
});

/**
 * 포켓몬 타입 아이콘 추가
 */
function setTypeIcon() {
   let pokemonList = $('.row.gutters').children('.pokemonCard');
   pokemonList.each((i, v) => {
      let innerHtml = '';
      let iconWrapp = $(v).find('.iconWrapp');

      let types = $(v).find('.types');
      types.each((i2, v2) => {
         let typeCount = types.length;
         let type = $(v2).val();
         let iconClass = i2 === 0 && typeCount > 1 ? 'icon first ' + type : 'icon ' + type;
         innerHtml += `
            <div class="${iconClass}" title="${type}">
                <img src="/svg/${type}.svg">
            </div>`
      });
      iconWrapp.append(innerHtml);
   });
}
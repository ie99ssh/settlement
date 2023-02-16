/**
 * @license Copyright (c) 2003-2019, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
    config.toolbar = [
        ['Font', 'FontSize'],
        ['TextColor', 'BGColor'],
        ['Bold', 'Italic', 'Underline', 'Strike'],
        ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'NumberedList', 'BulletedList'],
        ['Table', 'Smiley', 'SpecialChar'],
        ['tableinsert', 'tabledelete', 'tableproperties', '-', 'tablerowinsertbefore', 'tablerowinsertafter', 'tablerowdelete', '-', 'tablecolumninsertbefore', 'tablecolumninsertafter', 'tablecolumndelete', '-', 'tablecellproperties', 'tablecellsmerge', 'tablecellsplithorizontal', 'tablecellsplitvertical'],
        ['Maximize']
    ];
    config.htmlEncodeOutput = false;
    config.allowedContent = true;
};


CKEDITOR.config.font_names = '맑은 고딕; 돋움; 바탕; 돋음; 궁서;';

CKEDITOR.config.height = 400;

CKEDITOR.config.removePlugins = 'elementspath';

CKEDITOR.config.extraPlugins = 'tabletoolstoolbar,quicktable,tableresize';

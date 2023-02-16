CKEDITOR.plugins.add( 'simpleImageUpload', {
    icons: 'simpleimage',
    init: function( editor ) {
        var fileDialog = $('<input type="file" accept="image/gif, image/jpeg, image/png">');

        fileDialog.on('change', function (e) {
            var uploadUrl = editor.config.uploadUrl;
			var file = fileDialog[0].files[0];
			var imageData = new FormData();
			imageData.append('file', file);

			$.ajax({
				url: uploadUrl,
				type: 'POST',
				contentType: false,
				processData: false,
				data: imageData,
				beforeSend:function(){
                    COMMON.Ajax.fnAjaxBlock();
                }
			}).done(function(done) {
			    $.unblockUI();
			    if(done.retCode == 0){
    				var ele = editor.document.createElement('img');
    				ele.setAttribute('src', done.data.filePath + done.data.fileNm);
    				editor.insertElement(ele);

    				//첨부 이미지 정보 저장 (articleEdit.jsp에서 사용)
    				arrAttachInfo.push(done.data);
			    } else {
			        alert(done.message);
			    }
			}).fail(function(c){
                $.unblockUI();
                alert("업로드를 실패하였습니다.");
            });

        })
        editor.ui.addButton( 'SimpleImage', {
            label: '이미지 업로드',
            command: 'openDialog',
            toolbar: 'insert'
        });
        editor.addCommand('openDialog', {
            exec: function(editor) {
                fileDialog.val('');
                fileDialog.click();
            }
        })
    }
});

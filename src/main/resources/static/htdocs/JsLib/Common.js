/**------------------------------------------------------------
 * File Name      : common.js
 * Description    : BOQ Admin Custom Javascript Library
 * Author         : sangheon@payletter.com, 2018. 09. 28.
 * Modify History : Just Created.
 ------------------------------------------------------------*/

/**------------------------------------------------------------
 * BOQ Admin Global Variable
 ------------------------------------------------------------*/
let strAlertCallBack = "";
let gisCSRFToken     = false;
let strPreModalId    = "";
let isPreModalOpen   = false;

/**------------------------------------------------------------
 * JQuery Cached Query
 ------------------------------------------------------------*/
const C$ = function(query) {
    this.cache = this.cache || {};

    if (!this.cache[query]) {
        this.cache[query] = jQuery(query);
    }

    return this.cache[query];
};

/**------------------------------------------------------------
 * BOQ Admin Global Constants
 ------------------------------------------------------------*/
const COMMON = {
    CSRFID           : "RequestVerificationToken",
    BLOCKDIVID       : "divPageBlock",
    COMMONERRORMSG   : "Occured error.",
    EXCEPTIONMSG     : "예외가 발생하였습니다.",
    LOGINURL         : "/login",
    SUCCESSMSG       : "작업이 완료되었습니다.",
    FAILEDMSG        : "작업이 실패하였습니다.",
    SESSIONEXPIRECODE: 499,
    INVALIDAUTHCODE  : 401,
    PAGES            : 1,
    PAGELENGTH       : 20,
    LANGUAGE		 : {
        "sLengthMenu": '<select><option value="20">20</option><option value="50">50</option><option value="100">100</option></select> records per page'
    },
    EXTENSIONS       : ["jpg", "jpeg"],
    MAXUPLOADSIZE    : 5242880, // 5MB
    EXCEEDSIZEMSG    : "업로드 허용 용량을 초과하였습니다.",
}

/**------------------------------------------------------------
 * Request Type
 ------------------------------------------------------------*/
const REQUESTTYPE = {
    FORM: "form",
    AJAX: "ajax",
    JSON: "json"
}

/**------------------------------------------------------------
 * Edit Type
 ------------------------------------------------------------*/
const EDITTYPE = {
    REGIST: "regist",
    MODIFY: "modify"
}

/**------------------------------------------------------------
 * Auth Code
 ------------------------------------------------------------*/
const AUTHCODE = {
    ALL     : 1,
    READONLY: 2
}

/**------------------------------------------------------------
 * COMMON.Ajax
 ------------------------------------------------------------*/
COMMON.Ajax = {
    /**------------------------------------------------------------
     * Function Name  : fnRequest
     * Description    : Ajax Process
     * Author         : sangheon@payletter.com, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnRequest: function(objReq) {
        let intErrCnt = 0;
        const obj = {
            requestType  : null == objReq.requestType   ? REQUESTTYPE.JSON : objReq.requestType,
            requestData  : objReq.requestData,
            strCallUrl   : objReq.strCallUrl,
            strCallBack  : objReq.strCallBack,
            isModalMsg   : null == objReq.isModalMsg    ? false : objReq.isModalMsg,
            isaSync      : null == objReq.isaSync       ? true : objReq.isaSync,
            strSuccessMsg: null == objReq.strSuccessMsg ? COMMON.SUCCESSMSG : objReq.strSuccessMsg,
            strFailedMsg : null == objReq.strErrorMsg   ? COMMON.FAILEDMSG : objReq.strErrorMsg,
            objJson: {
                intRetCode: 9999,
                strRetMsg : COMMON.EXCEPTIONMSG
            }
        }

        $.ajax({
            cache: false,
            async: obj.isaSync,
            type: "POST",
            data: (obj.requestType == REQUESTTYPE.JSON ? JSON.stringify(obj.requestData) : obj.requestData),
            url: obj.strCallUrl,
            dataType: "JSON",
            contentType: (obj.requestType == REQUESTTYPE.JSON ? "application/json; charset=utf-8" : "application/x-www-form-urlencoded; charset=UTF-8"),
            headers: { "RequestVerificationToken": COMMON.AntiCSRF.getVerificationToken() },
            beforeSend: function () {
                if (objReq.noLoading == undefined) {
                    COMMON.Ajax.fnAjaxBlock();
                }
            },
            complete: function () {
                if (objReq.noLoading == undefined) {
                    $.unblockUI();
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                intErrCnt++;
                if (XMLHttpRequest.status == COMMON.SESSIONEXPIRECODE
                    || XMLHttpRequest.status == COMMON.INVALIDAUTHCODE) {
                    location.href = COMMON.LOGINURL;
                    return;
                }

                if (intErrCnt == 3 && XMLHttpRequest.readyState == 4 && textStatus == "parsererror") {
                    COMMON.Ajax.fnRequestResult(obj);
                } else if (intErrCnt == 3 && XMLHttpRequest.readyState == 0 && textStatus == "error") {
                    COMMON.Ajax.fnRequestResult(obj);
                }
            }
        }).retry({ times: 3, timeout: 1000 }).then(function(objJson) {
            try {
                if (typeof (objJson) === "object") {
                    obj.objJson = objJson;
                } else if (typeof (objJson) === "string") {
                    obj.objJson = jQuery.parseJSON(objJson);
                } else {
                    if (intErrCnt == 3) {
                        COMMON.Ajax.fnRequestResult(obj);
                    }

                    return;
                }

                if(obj.isModalMsg) {
                    COMMON.Ajax.fnRequestResult(obj);
                }else {
                    eval(obj.strCallBack)(objJson, obj.requestData);
                }
            } catch(ex) {}
        });
    },

    /**------------------------------------------------------------
     * Function Name  : fnExcelDownload
     * Description    : Ajax Process
     * Author         : sangheon@payletter.com, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnExcelDownload: function(objReq) {
        let intErrCnt = 0;
        const obj = {
            requestData  : objReq.requestData,
            strCallUrl   : objReq.strCallUrl,
            strCallBack  : objReq.strCallBack,
            objJson: {
                intRetCode: 9999,
                strRetMsg : COMMON.EXCEPTIONMSG
            }
        }

        $.ajax({
            type: "POST",
            url: obj.strCallUrl,
            responseType: "blob",
            headers: {
                "RequestVerificationToken": COMMON.AntiCSRF.getVerificationToken(),
                'Accept': 'application/vnd.ms-excel'
            },
            data: (obj.requestType == REQUESTTYPE.JSON ? JSON.stringify(obj.requestData) : obj.requestData),
            dataType: "JSON",
            beforeSend: function () {
                if (objReq.noLoading == undefined) {
                    COMMON.Ajax.fnAjaxBlock();
                }
            },
            complete: function () {
                if (objReq.noLoading == undefined) {
                    $.unblockUI();
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                intErrCnt++;
                if (XMLHttpRequest.status == COMMON.SESSIONEXPIRECODE
                    || XMLHttpRequest.status == COMMON.INVALIDAUTHCODE) {
                    location.href = COMMON.LOGINURL;
                    return;
                }

                if (intErrCnt == 3 && XMLHttpRequest.readyState == 4 && textStatus == "parsererror") {
                    COMMON.Ajax.fnRequestResult(obj);
                } else if (intErrCnt == 3 && XMLHttpRequest.readyState == 0 && textStatus == "error") {
                    COMMON.Ajax.fnRequestResult(obj);
                }
            }
        }).retry({ times: 3, timeout: 1000 }).then(function(objJson) {
            try {
                if (typeof (objJson) === "object") {
                    obj.objJson = objJson;
                } else if (typeof (objJson) === "string") {
                    obj.objJson = jQuery.parseJSON(objJson);
                } else {
                    if (intErrCnt == 3) {
                        COMMON.Ajax.fnRequestResult(obj);
                    }

                    return;
                }

                if(obj.isModalMsg) {
                    COMMON.Ajax.fnRequestResult(obj);
                }else {
                    eval(obj.strCallBack)(objJson, obj.requestData);
                }
            } catch(ex) {}
        });
    },

    /**------------------------------------------------------------
     * Function Name  : fnAjaxBlock
     * Description    : Ajax Block
     * Author         : sangheon@payletter.com, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnAjaxBlock: function() {
        try {
            if($("#" + COMMON.BLOCKDIVID).is(':visible')) {
                return;
            }

            $.blockUI({
                message: $("#" + COMMON.BLOCKDIVID),
                css: {
                    top: '50%',
                    left: '50%',
                    padding: 0,
                    margin: 0,
                    width: '24px',
                    height: '24px',
                    border: 'none',
                    backgroundColor: 'transparent',
                    '-webkit-border-radius': '10px',
                    '-moz-border-radius': '10px',
                    opacity: 1,
                    color: '#000',
                    'z-index': 9999
                }, overlayCSS: {
                    backgroundColor: '#FFFFFF',
                    opacity: 0.3,
                    cursor: 'wait',
                    'z-index': 9998
                }
            });
        } catch(ex) { }
    },

    /**------------------------------------------------------------
     * Function Name  : CreateDataTable
     * Description    : -
     * Author         : sangheon@payletter.com, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    CreateDataTable: function(strLocation, isPartial, opts) {
        // 테이블아이디, 부분조회여부, DataTables옵션
        // 기본옵션 상속받기 (옵션 우선순위 : 고정옵션 > 호출옵션 > 기본옵션)
        const conf = $.extend({
            pages: COMMON.PAGES,
            pageLength: COMMON.PAGELENGTH,
            method: 'POST',
            dom: '<"top"l>frtip',
            resizable: true,
            lengthChange: true,
            searching: true,
            pagingType: "simple_numbers",
            oLanguage : COMMON.LANGUAGE
            //responsive: isResponsive
        }, opts);

        const fnOrgDrawCallback = conf.fnDrawCallback;

        // Callback 구현
        conf.fnDrawCallback = function (settings) {
            $(strLocation).DataTable().columns.adjust();
            if (typeof (fnOrgDrawCallback) == "function") {
                fnOrgDrawCallback(settings);
            }
        }

        if (isPartial) {
            //부분조회시 고정 옵션 - 수정금지
            conf.processing = false;
            conf.serverSide = true;
            conf.sort = true;
            conf.order = [];
            conf.searching = false;
            conf.ajax = COMMON.Ajax.Pipeline({
                url: conf.url,
                data: conf.data,
                pages: conf.pages
            });
        } else {
            //전체조회시 고정 옵션- 수정금지
            conf.processing = false;
            conf.serverSide = false;
            conf.sort = true;
            conf.order = [];
            conf.ajax = function (data, callback, settings) {
                $.ajax({
                    type: conf.method,
                    url: conf.url,
                    dataType: "JSON",
                    data: JSON.stringify(conf.data()),
                    contentType: "application/json; charset=utf-8",
                    headers: { "RequestVerificationToken": COMMON.AntiCSRF.getVerificationToken() },
                    beforeSend: function() {
                        COMMON.Ajax.fnAjaxBlock();
                    },
                    complete: function() {
                        $.unblockUI();
                    },
                    success: function(json) {
                        if (json.subdata != null) {
                            eval(json.subdata.strCallBack)(json.subdata);
                        }

                        if (json.intRetCode != 0) {
                            COMMON.Msg.fnAlert(json.strRetMsg);
                        } else {
                            settings.json = json;
                            callback(json);
                        }
                    },
                    error: function(xhr, error, thrown) {
                        const log = settings.oApi._fnLog;

                        if (xhr.status == COMMON.SESSIONEXPIRECODE
                            || xhr.status == COMMON.INVALIDAUTHCODE) {
                            location.href = COMMON.LOGINURL;
                            return;
                        }

                        if (error == "parsererror") {
                            log(settings, 0, 'Invalid JSON response', 1);
                        } else if (xhr.readyState === 4) {
                            log(settings, 0, 'Ajax error', 7);
                        }
                    }
                });
            };
        }

        return $(strLocation).DataTable(conf);
    },

    /**------------------------------------------------------------
     * Function Name  : Pipeline
     * Description    : -
     * Author         : sangheon@payletter.com, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    Pipeline: function(opts) {
        const conf = $.extend({
            method: 'POST'
        }, opts);

        let cacheLower       = -1;
        let cacheUpper       = null;
        let cacheLastRequest = null;
        let cacheLastJson    = null;

        return function (request, drawCallback, settings) {
            let ajax = false;
            let requestStart  = request.start;
            const drawStart     = request.start;
            const requestLength = request.length;
            const requestEnd    = requestStart + requestLength;

            if (settings.clearCache) {
                ajax = true;
                settings.clearCache = false;
            } else if (cacheLower < 0 || requestStart < cacheLower || requestEnd > cacheUpper) {
                ajax = true;
            } else if (JSON.stringify(request.order) !== JSON.stringify(cacheLastRequest.order) ||
                JSON.stringify(request.columns) !== JSON.stringify(cacheLastRequest.columns) ||
                JSON.stringify(request.search) !== JSON.stringify(cacheLastRequest.search)) {
                ajax = true;
            }

            cacheLastRequest = $.extend(true, {}, request);

            if (ajax) {
                if (requestStart < cacheLower) {
                    requestStart = requestStart - (requestLength * (conf.pages - 1));

                    if (requestStart < 0) {
                        requestStart = 0;
                    }
                }

                cacheLower = requestStart;
                cacheUpper = requestStart + (requestLength * conf.pages);

                request.start = requestStart;
                request.length = requestLength * conf.pages;

                if ($.isFunction(conf.data)) {
                    const d = conf.data(request);

                    if (d) {
                        $.extend(request, d);
                    }
                } else if ($.isPlainObject(conf.data)) {
                    $.extend(request, conf.data);
                }

                delete request.columns;
                delete request.search;
                delete request.order;

                settings.jqXHR = $.ajax({
                    "type": conf.method,
                    "url": conf.url,
                    "data": JSON.stringify(request),
                    "dataType": "json",
                    "contentType" : "application/json; charset=utf-8",
                    "headers": { "RequestVerificationToken": COMMON.AntiCSRF.getVerificationToken() },
                    "cache": false,
                    "success": function(json) {
                        cacheLastJson = $.extend(true, {}, json);

                        if (cacheLower != drawStart) {
                            json.data.splice(0, drawStart - cacheLower);
                        }

                        json.data.splice(requestLength, json.data.length);

                        settings.json = json;

                        drawCallback(json);
                    },
                    beforeSend: function() {
                        COMMON.Ajax.fnAjaxBlock();
                    },
                    complete: function() {
                        $.unblockUI();
                    },
                    error: function(xhr, error, thrown) {
                        const log = settings.oApi._fnLog;

                        if (xhr.status == COMMON.SESSIONEXPIRECODE
                            || xhr.status == COMMON.INVALIDAUTHCODE) {
                            location.href = COMMON.LOGINURL;
                            return;
                        }

                        if (error == "parsererror") {
                            log(settings, 0, 'Invalid JSON response', 1);
                        } else if (xhr.readyState === 4) {
                            log(settings, 0, 'Ajax error', 7);
                        }
                    }
                });
            } else {
                const json = $.extend(true, {}, cacheLastJson);

                json.draw = request.draw;
                json.data.splice(0, requestStart - cacheLower);
                json.data.splice(requestLength, json.data.length);

                drawCallback(json);
            }
        }
    },

    fnRequestResult: function(obj) {
        if("0" == obj.objJson.intRetCode) {
            COMMON.Msg.fnAlert(obj.strSuccessMsg, obj.strCallBack, obj);
        } else {
            COMMON.Msg.fnAlert(obj.strFailedMsg + "(" + obj.objJson.strRetMsg + ")");
        }
    }
}

/**------------------------------------------------------------
 * COMMON.Utils
 ------------------------------------------------------------*/
COMMON.Utils = {
    /**------------------------------------------------------------
     * Function Name  : fnSetValidate
     * Description    : -
     * Author         : sangheon@payletter.com, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnSetValidate: function(arrValidate) {
        $('#' + arrValidate["FORMID"]).validate({
            focusInvalid: false,
            ignore: "",
            rules: arrValidate["VARIABLE"],
            invalidHandler: function (event, validator) {
            },
            errorPlacement: function (label, element) { // render error placement for each input type
                const icon = $(element).parent('.input-with-icon').children('i');
                const parent = $(element).parent('.input-with-icon');

                icon.removeClass('fa fa-check').addClass('fa fa-exclamation');
                parent.removeClass('success-control').addClass('error-control');

                parent.attr({
                    "data-placement": 'top'
                    ,"data-trigger" : 'manual'
                    ,"data-original-title" : label[0].textContent
                }).tooltip("show");
            },
            highlight: function (element) {
                const parent = $(element).parent();
                parent.removeClass('success-control').addClass('error-control');
            },
            unhighlight: function (element) {
            },
            success: function (label, element) {
                const icon = $(element).parent('.input-with-icon').children('i');
                const parent = $(element).parent('.input-with-icon');

                icon.removeClass("fa fa-exclamation").addClass('fa fa-check');
                parent.removeClass('error-control').addClass('success-control');

                parent.tooltip("hide").removeAttr("data-placement data-trigger data-original-title");
            },
            submitHandler: function (form) {
            }
        });

        $('.select2', "#" + arrValidate["FORMID"]).change(function () {
            $('#' + arrValidate["FORMID"]).validate().element($(this));
        });
    },

    /**------------------------------------------------------------
     * Function Name  : fnInitSearchDateTimePicker
     * Description    : Initialize Start and End DateTimePicker for Search
     * Author         : sangheon, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnInitSearchDateTimePicker: function(strStartDateDiv, strEndDateDiv, opts) {
        const $objStartDateDiv = $(strStartDateDiv);
        const $objEndDateDiv   = $(strEndDateDiv);

        const $objStartDateInput = $objStartDateDiv.find("input");
        const $objEndDateInput   = $objEndDateDiv.find("input");

        // 기본옵션 상속 (우선순위: 호출옵션 > 기본옵션)
        const objStartDateConfig = $.extend({
            format: "YYYY-MM-DD",
            autoclose: true,
            pickTime : false,
            maxDate: $objEndDateInput.val()
        }, opts);

        const objEndDateConfig = $.extend({
            format:"YYYY-MM-DD",
            autoclose: true,
            pickTime : false,
            minDate: $objStartDateInput.val()
        }, opts);

        // Date picker 생성
        $objStartDateDiv.datetimepicker(objStartDateConfig);
        $objEndDateDiv.datetimepicker(objEndDateConfig);

        const objStartDatePicker = $objStartDateDiv.data("DateTimePicker");
        const objEndDatePicker   = $objEndDateDiv.data("DateTimePicker");

        // 날짜 변경 시 트리거로 이벤트 생성
        $objStartDateInput.on("focusout", function(e) {
            $objStartDateDiv.trigger("dp.change");
        });
        $objEndDateInput.on("focusout", function(e) {
            $objEndDateDiv.trigger("dp.change");
        });
        $objStartDateInput.on("keyup", function(e) {
            if (this.value.length == this.maxLength) {
                $objStartDateDiv.trigger("dp.change");
            }
        });
        $objEndDateInput.on("keyup", function(e) {
            if (this.value.length == this.maxLength) {
                $objEndDateDiv.trigger("dp.change");
            }
        });

        // 데이터 변경 시 유효성 확인: 잘못된 날짜 입력 시 이전 날짜로 다시 복구
        $objStartDateDiv.on("dp.change", function(e){
            if (objStartDatePicker.getDate() === null) {
                objStartDatePicker.setDate(objStartDatePicker.date);
            }
            objEndDatePicker.setMinDate(e.date); // 시작날짜 변경에 따라 종료날짜의 최소 날짜 변경
        });
        $objEndDateDiv.on("dp.change", function(e){
            if (objEndDatePicker.getDate() === null) {
                objEndDatePicker.setDate(objEndDatePicker.date);
            }
            objStartDatePicker.setMaxDate(e.date); // 종료날짜 변경에 따라 시작날짜의 최대 날짜 변경
        });
    },

    /**------------------------------------------------------------
     * Function Name  : fnInitSingleDateTimePicker
     * Description    : Set Date Time Picker
     * Author         : sangheon, 2018. 09. 28
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnInitSingleDateTimePicker: function(strDateDiv, strFormat, opts, isRestore) {
        const $objDateDiv = $(strDateDiv);
        const $objDate    = $objDateDiv.find("input");

        //기본 옵션 상속 받기 (옵션 우선 순위: 호출 옵션 > 기본 옵션)
        const confDate = $.extend({
            format: strFormat,
            autoclose: true,
            pickTime : true,
            //maxDate : $objDate.val();
        }, opts);

        isRestore = (isRestore == undefined ? true : fnGetBoolean(isRestore))

        //Datetimepicker 세팅
        $objDateDiv.datetimepicker(confDate);

        const objDatePicker = $objDateDiv.data("DateTimePicker");

        $objDate.on("focusout", function(e) {
            $objDateDiv.trigger("dp.change");
        });

        $objDate.on("keyup", function(e) {
            if(this.value.length == this.maxLength) {
                $objDateDiv.trigger("dp.change");
            }
        });

        $objDateDiv.on("dp.change", function(e) {
            if(isRestore && objDatePicker.getDate() === null) objDatePicker.setDate(objDatePicker.date);
        });
    },

    /**------------------------------------------------------------
     * Function Name  : fnFileDownload
     * Description    : 파일 다운로드
     * Author         : sangheon, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnFileDownload: function(strHttpMethod, requestData, strCallUrl,strCallBack) {
        let arrParams;

        if (strHttpMethod.toUpperCase() == "POST") {
            arrParams = { "RequestVerificationToken": COMMON.AntiCSRF.getVerificationToken() };
            $.extend(arrParams, requestData);
        } else {
            arrParams = requestData;
        }

        $.fileDownload(strCallUrl, {
            httpMethod: strHttpMethod,
            data: arrParams,
            prepareCallback: function(url) {
                //COMMON.Ajax.fnAjaxBlock();
            },
            successCallback: function(url) {
                //$.unblockUI();
                if(strCallBack != undefined && strCallBack != "") {
                    eval(strCallBack)({
                        intRetCode : 0,
                        strRetMsg : "OK"
                    });
                }
            },
            failCallback: function(html, url) {
                //$.unblockUI();
                COMMON.Msg.fnAlert(COMMON.COMMONERRORMSG);
            }
        });
    },

    /**------------------------------------------------------------
     * Function Name  : fnRequestFile
     * Description    : Ajax Process For File Upload
     * Author         : sangheon@payletter.com, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnRequestFile: function ($objFormData, strCallUrl, strCallBack) {
        const obj = {
            intRetCode: 9998,
            strRetMsg : COMMON.EXCEPTIONMSG
        };

        $.ajax({
            cache: false,
            type: 'POST',
            data: $objFormData,
            url: strCallUrl,
            contentType: false,
            processData: false,
            headers: { "RequestVerificationToken": COMMON.AntiCSRF.getVerificationToken() },
            beforeSend: function () {
                COMMON.Ajax.fnAjaxBlock();
            },
            complete: function () {
                $.unblockUI();
            },
            success: function (objJson) {
                eval(strCallBack)(objJson);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (XMLHttpRequest.status == COMMON.SESSIONEXPIRECODE
                    || XMLHttpRequest.status == COMMON.INVALIDAUTHCODE) {
                    location.href = COMMON.LOGINURL;
                    return;
                }

                eval(strCallBack)(obj);
            }
        });
    },

    /**------------------------------------------------------------
     * Function Name  : fnFrmReset
     * Description    : -
     * Author         : sangheon@payletter.com, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnFrmReset: function(frmId) {
        $("form").each(function() {
            if(this.id == frmId) {
                this.reset();

                $(this).find("select").each(function(){
                    const defaultValue = $(this).find("option:eq(0)").val();
                    $(this).select2("val", defaultValue);
                });

                $(this).find("input[type=hidden]").each(function () {
                    this.value = "";
                });
            }
        });

        try {
            const parent = $('.input-with-icon');
            parent.children('i').removeClass("fa fa-exclamation").removeClass('fa fa-check');
            parent.removeClass('error-control').removeClass('success-control');
            parent.tooltip("hide").removeAttr("data-placement data-trigger data-original-title");
        } catch(ex) { }
    },

    fnRenderFlag: function(data) {
        return data ? "Y" : "<font color='red'>N</font>";
    },

    /**------------------------------------------------------------
     * Function Name  : fnSetAutocomplete
     * Description    : Set Autocomplete for input box
     * Author         : sangheon, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnSetAutocomplete: function (objParam) {
        $(objParam.targetId).autocomplete({
            minLength: 1,
            //source: objParam.source,
            source: function(request, response) {
                const results = $.ui.autocomplete.filter(objParam.source, request.term);
                response(results.slice(0, 20));
            },
            appendTo: $(objParam.appendId),
            delay:0,
            focus: function(event, ui) {
                return false;
            },
            select: function(event, ui) {
                C$(objParam.valueId).val(ui.item.value);
                C$(objParam.labelId).val(ui.item.label);
                C$(objParam.targetId).val(ui.item.label);
                return false;
            }
        })
            .autocomplete("instance")._renderItem = function(ul, item) {
            return $("<li>")
                .append("<div>" + item.desc + "</div>")
                .appendTo(ul);
        };

        $(objParam.targetId).on("blur", function() {
            $(this).val($(objParam.labelId).val());
            $(this).valid();
        });
    },

    /**------------------------------------------------------------
     * Function Name  : fnserializeIncludeDisabled
     * Description    : Set serialize for Disabled Elements
     * Author         : sangheon, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnserializeIncludeDisabled: function (obj) {
        const disabled = obj.find(":disabled").removeAttr("disabled");
        const serializedObject = obj.serializeObject();
        disabled.prop("disabled", true);
        return serializedObject;
    },

    /**------------------------------------------------------------
     * Function Name  : fnSortSettings
     * Description    : 정렬기능 세팅 (페이징 처리 limit 사용)
     * Author         : sangheon, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnSortSettings : function (obj, formObj) {

        const columnId = $(obj).attr("id");
        const araLabel = $(obj).attr("aria-label");
        let sortType = "";

        if (araLabel.match("ascending")) {
            sortType = "ASC";
        } else if (araLabel.match("descending")) {
            sortType = "DESC";
        }

        formObj.find("[name=strSortColumn]").val(columnId);
        formObj.find("[name=strSortType]").val(sortType);

    },

    /**------------------------------------------------------------
     * Function Name  : fnRenderComma
     * Description    : Render Comma for Number
     * Author         : kckim, 2018. 04. 06.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnRenderComma: function(data) {
        if (data == null) return "-";

        const arrNumber = Number(data).toFixed(2).toString().split('.');
        let strRetVal;

        arrNumber[0] = arrNumber[0].replace(/(\d)(?=(?:\d{3})+(?!\d))/g,'$1,');

        if (arrNumber[1] == "00") {
            strRetVal = arrNumber[0];
        } else {
            strRetVal = arrNumber[0] + "." + arrNumber[1];
        }
        return strRetVal;
    },

    /**------------------------------------------------------------
     * Function Name  : fnRenderStrDateFormat
     * Description    : Render Str for Date
     * Author         : sangheon, 2018. 11. 26.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnRenderStrDateFormat: function(data) {
        if (data == null) return "-";

        data = String(data);

        const delimeter = ".";
        const yyyy = data.substr(0, 4);
        const mm = data.substr(4, 2);
        const dd = data.substr(6, 2);
        const strRetVal = yyyy + delimeter + mm + delimeter + dd;

        return strRetVal;
    },

    /**------------------------------------------------------------
     * Function Name  : fnRenderOrgFormat
     * Description    : Render Str for Date
     * Author         : sangheon, 2018. 11. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnRenderOrgFormat : function(data) {
        if (data == null) return "-";

        data = String(data);
        const strRetVal = data.replace(/,/gi, "");

        return strRetVal;
    },

    /**------------------------------------------------------------
     * Function Name  : fnReplaceAll
     * Description    : Replace All
     * Author         : kckim, 2018. 12. 04.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnReplaceAll : function(str, searchStr, replaceStr) {
        return str.split(searchStr).join(replaceStr);
    },

    /**------------------------------------------------------------
     * Function Name  : fnResetFile
     * Description    : Reset Input file
     * Author         : kckim, 2018. 04. 26.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnResetFile: function(fileId) {
        const $temp = $(fileId);
        $temp.wrap("<form>").closest("form").get(0).reset();
        $temp.unwrap();
    },

    /**------------------------------------------------------------
     * Function Name  : fnRequestFileCheck
     * Description    : Ajax Process For File Upload2
     * Author         : kckim, 2018. 04. 03.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnRequestFileCheck: function (objReq) {
        const obj = {
            intRetCode: 9998,
            strRetMsg : COMMON.EXCEPTIONMSG,
            isModalMsg   : null == objReq.isModalMsg    ? false : objReq.isModalMsg,
            strSuccessMsg: null == objReq.strSuccessMsg ? COMMON.SUCCESSMSG : objReq.strSuccessMsg,
            strFailedMsg : null == objReq.strErrorMsg   ? COMMON.FAILEDMSG : objReq.strErrorMsg
        };

        if (objReq.intCheckSize > 0 && objReq.objFileData.size > objReq.intCheckSize) {
            COMMON.Msg.fnAlert(COMMON.EXCEEDSIZEMSG);
            return;
        }

        if (objReq.arrExtension && objReq.arrExtension.length != 0 && $.inArray(objReq.objFileData.name.split('.').pop().toLowerCase(), objReq.arrExtension) == -1) {
            COMMON.Msg.fnAlert(COMMON.EXTENSIONCHKMSG);
            return;
        }

        $.ajax({
            cache: false,
            type: 'POST',
            data: objReq.objFormData,
            url: objReq.strCallUrl,
            contentType: false,
            processData: false,
            headers: { "RequestVerificationToken": COMMON.AntiCSRF.getVerificationToken() },
            beforeSend: function () {
                COMMON.Ajax.fnAjaxBlock();
            },
            complete: function () {
                $.unblockUI();
            },
            success: function (objJson) {
                eval(objReq.strCallBack)(objJson);
                if(obj.isModalMsg) COMMON.Msg.fnAlert(obj.strSuccessMsg);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                if (XMLHttpRequest.status == COMMON.SESSIONEXPIRECODE
                    || XMLHttpRequest.status == COMMON.INVALIDAUTHCODE) {
                    location.href = COMMON.LOGINURL;
                    return;
                }

                if (XMLHttpRequest.status == COMMON.METHODNOTALLOWED) {
                    COMMON.Msg.fnAlert(XMLHttpRequest.responseJSON.message);
                    return;
                }

                eval(objReq.strCallBack)(obj);
                if(obj.isModalMsg) COMMON.Msg.fnAlert(obj.strFailedMsg);
            }
        });
    },

    /**------------------------------------------------------------
     * Function Name  : fnNullToEmpty
     * Description    : null인 경우 공백처리
     * Author         : sangheon, 2019. 01. 08.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnNullToEmpty : function(str) {

        let retStr = "";

        if (str != null) {
            retStr = str;
        }

        return retStr;
    },

    /**------------------------------------------------------------
     * Function Name  : fnErrorInfoListToHtml
     * Description    : BulkProcResult.List<ErrorInfo> 객체를 html 문자열로 컨버팅
     * Author         : sh_shin, 2019. 02. 22.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnErrorInfoListToHtml : function(objErrorInfoList) {
        const arrHtml = [];
        for(let i = 0; i < objErrorInfoList.length; i++) {
            const strMessage = "에러 Sequence : {0} <br/>에러 메시지 : {1} ({2})".format(objErrorInfoList[i].lngSeqNo, objErrorInfoList[i].strErrorMsg, objErrorInfoList[i].intErrorCode);
            arrHtml.push(strMessage)
        }

        return arrHtml.join("<div class='divideline'></div>");
    },
    /**------------------------------------------------------------
     * Function Name  : fnIsNumberHipun
     * Description    : Render Str for Date
     * Author         : sangheon, 2019. 04. 16.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnIsNumberHipun : function(data) {
        if (data == null) return null;

        data = COMMON.Utils.fnReplaceAll(data, "-", "");

        const intData = Number(data);

        if(!isNaN(intData) && intData != "") {
            return true;
        } else {
            return false;
        }
    },

    fnPhoneFormatRender : function(data) {
        if (data == null) return null;
        return data.replace(/(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})/, "$1-$2-$3");
    }
}

/**------------------------------------------------------------
 * COMMON.Msg
 ------------------------------------------------------------*/
COMMON.Msg = {
    /**------------------------------------------------------------
     * Function Name  : fnAlert
     * Description    : -
     * Author         : sangheon@payletter.com, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnAlert: function (strMsg, strCallBack, objCallBackParam, strMsgDetail) {
        $("body").addClass("breakpoint-1024 pace-done modal-open ");
        $("#modalMsg").text(strMsg);
        $("#modalDetailMsg").empty();

        if(strMsgDetail != undefined && strMsgDetail != ""){
            $("#alertModal #modalDetailMsg").html(strMsgDetail);
            $("#modalDetailDiv").show();
        }else {
            $("#modalDetailDiv").hide();
        }

        $("#alertModal #btnAlertModalBottom").unbind("click");

        if(null != strCallBack && "" != strCallBack ) {
            $("#alertModal #btnAlertModalBottom").bind("click", function(){
                eval(strCallBack)(objCallBackParam.objJson, objCallBackParam.requestData);
            });
        }

        $("#btnModalMsg").click();
    },

    /**------------------------------------------------------------
     * Function Name  : fnConfirm
     * Description    : -
     * Author         : sangheon@payletter.com, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnConfirm: function (strMsg, callBackFN, cancelCallBackFN) {
        $("body").addClass("breakpoint-1024 pace-done modal-open ");
        strAlertCallBack = callBackFN;

        $("#btnConfirmModalBottom").off("click");

        if(cancelCallBackFN != null && cancelCallBackFN != "") {
            $("#btnConfirmModalBottom").on("click", cancelCallBackFN);
        }

        $("#modalConfirm").html(strMsg);
        $("#btnModalConfirm").click();
    },

    /**------------------------------------------------------------
     * Function Name  : fnLoginConfirm
     * Description    : -
     * Author         : sangheon@payletter.com, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnLoginConfirm: function ($objData, callBackFN) {
        $("body").addClass("breakpoint-1024 pace-done modal-open ");
        strAlertCallBack = callBackFN;
        $("#spFinalAccessTime").text($objData.data["lastLoginDate"]);
        $("#spFinalAccessIp").html($objData.data["lastLoginIP"]);
        $("#btnModalLoginConfirm").click();
    },

    /**------------------------------------------------------------
     * Function Name  : fnAlertWithModal
     * Description    : -
     * Author         : sangheon@payletter.com, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnAlertWithModal: function (strMsg, preModalId, preModalOpen, callBackFN) {
        strAlertCallBack = typeof callBackFN == "undefined" ? function() {} : callBackFN;
        strPreModalId  = preModalId;
        isPreModalOpen = preModalOpen;
        $("#" + preModalId).modal("hide");

        $("body").addClass("breakpoint-1024 pace-done modal-open ");
        $("#modalMsg").text(strMsg);
        $("#btnModalMsg").click();
    },

    /**------------------------------------------------------------
     * Function Name  : fnConfirmWithModal
     * Description    : -
     * Author         : sangheon@payletter.com, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnConfirmWithModal: function (strMsg, callBackFN, preModalId, preModalOpen) {
        strPreModalId  = preModalId;
        isPreModalOpen = preModalOpen;
        $("#" + preModalId).modal("hide");

        $("body").addClass("breakpoint-1024 pace-done modal-open ");
        strAlertCallBack = callBackFN;
        $("#modalConfirm").html(strMsg);
        $("#btnModalConfirm").click();
    },

    /**------------------------------------------------------------
     * Function Name  : fnResetBodyClass
     * Description    : -
     * Author         : sangheon@payletter.com, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnResetBodyClass: function () {
        $("body").removeClass();
        $("body").addClass("breakpoint-1024 pace-done ");
    }
}

/**------------------------------------------------------------
 * COMMON.AntiCSRF
 ------------------------------------------------------------*/
COMMON.AntiCSRF = {
    /**------------------------------------------------------------
     * Function Name  : getVerificationToken
     * Description    : Get CSRF Token
     * Author         : sangheon@payletter.com, 2018. 09. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    getVerificationToken: function() {
        let strCSRFValue = "";

        if (!gisCSRFToken) {
            $.ajax({
                cache : false,
                type  : 'POST',
                url   : "/login/getCSRFToken",
                async : false,
                success: function (objJson) {
                    strCSRFValue = $.trim(objJson.CSRFToken);
                    $("#" + COMMON.CSRFID).val(strCSRFValue);
                }
            });

            gisCSRFToken = true;
        } else {
            strCSRFValue = $("#" + COMMON.CSRFID).val();
        }

        return strCSRFValue;
    }
}

/**------------------------------------------------------------
 * COMMON.Auth
 ------------------------------------------------------------*/
COMMON.Auth = {
    AuthCode: null,
    AuthDLCode: null,

    /**------------------------------------------------------------
     * Function Name  : Init
     * Description    : Auth 초기화
     * Author         : jaden, 2018. 04. 20.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    Init: function (pageAuthCode, pageAuthDLCode) {
        this.AuthCode = pageAuthCode;       // 현재 접근한 메뉴에 대한 사용자의 권한
        this.AuthDLCode = pageAuthDLCode;   // 현재 접근한 메뉴에 대한 사용자의 다운로드 권한
        this.fnShowAndHide();
    },

    // 초기화 재수행 - 동적 html 처리시 필요한 경우 직접 호출..
    Refresh: function () {
        this.fnShowAndHide();
    },

    // 버튼/Layer 등 Show/Hide 수행
    fnShowAndHide: function () {
        // ALL 권한일 경우 auth-readonly hide
        if (this.AuthCode === AUTHCODE.ALL) {
            $(".auth-all").show();
            $(".auth-readonly").hide();

            // READONLY 권한일 경우 auth-all hide
        } else if (this.AuthCode === AUTHCODE.READONLY) {
            $(".auth-readonly").show();
            $(".auth-all").hide();

            // 그 외 지정되지 않은 값은 모두 hide
        } else {
            $(".auth-readonly").hide();
            $(".auth-all").hide();
        }

        // 엑셀 다운로드 권한에 따른 버튼 show/hide
        if (this.AuthDLCode) {
            $(".auth-dl").show();
        } else{
            $(".auth-dl").hide();
        }
    },

    // 접근한 사용자 권한이 지정한 권한을 포함 하는지 여부 확인(AuthCode는 낮은 숫자가 높은 권한)
    fnCheckHasAuth: function(intAuthCode, intAvailAuthCode) {
        return intAuthCode <= intAvailAuthCode;
    }
}

/**------------------------------------------------------------
 * Function Name  : serializeObject
 * Description    : Get Serialize Object
 * Author         : sangheon@payletter.com, 2018. 09. 28
 * Modify History : Just Created.
 ------------------------------------------------------------*/
$.fn.serializeObject = function () {
    const o = {};
    const a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });

    return o;
};

/**------------------------------------------------------------
 * Function Name  : dataTable.Api.register
 * Description    : Data Table Pipeline
 * Author         : sangheon@payletter.com, 2018. 09. 28
 * Modify History : Just Created.
 ------------------------------------------------------------*/
$.fn.dataTable.Api.register('clearPipeline()', function () {
    return this.iterator('table', function (settings) {
        settings.clearCache = true;
    });
});

/**------------------------------------------------------------
 * validation
 ------------------------------------------------------------*/
$(function () {
    $.validator.addMethod("notEqualTo", function (value, element, param) {
        return this.optional(element) || value != $(param).val();
    }, "Please specify a different value.");

    $.validator.addMethod("dupId", function (value, element, param) {
        return this.optional(element) || "N" != $(param).val();
    }, "Please specify a different Id or No.");

    $.validator.addMethod("alphaNumeric", function (value, element, param) {
        return this.optional(element) || /^[a-zA-Z0-9]+$/.test(value);
    }, "Please input alphanumeric characters only for Id.");

    $.validator.addMethod("passwordCheck", function (value, element, param) {
        return this.optional(element) || /^.*(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$/.test(value);
    }, "Please enter a valid password value.(character + number + special character)");

    $.validator.addMethod("pwCheckConsecChars", function (value, element, param) {
        return this.optional(element) || !/(.)\1{2,}/.test(value);
    }, "The password must not contain 3 consecutive identical characters.");

    $.validator.addMethod("rate", function (value, element, param) {
        return this.optional(element) || /^(?=.)(?:[0-9]\d{0,2})?(?:\.\d{1,2})?$/.test(value);
    }, "Please enter a valid rate value.(decimal point is allowed up to 2)");

    $.validator.addMethod("noKorean", function (value, element, param) {
        return this.optional(element) || !/[\u3131-\u314e|\u314f-\u3163|\uac00-\ud7a3]/.test(value);
    }, "Korean is not allowed in this field.");

    $.validator.addMethod("yyyymmdd", function (value, element, param) {
        return this.optional(element) || !/^[12][0-9]{3}[01][0-9][0-3][0-9]$/.test(value);
    }, "Invalid date format.");

    $.validator.addMethod("celNo", function (value, element, param) {
        return this.optional(element) || /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/.test(value);
    }, "Please input numeric and '-' characters only for cell phone number.");
});

/**------------------------------------------------------------
 * String 확장 메소드
 ------------------------------------------------------------*/
String.prototype.format = function(){
    let strString = this;

    for(const arg in arguments){
        const re = new RegExp("\\{" + arg + "\\}", "gm");
        strString = strString.replace(re, arguments[arg]);
    }

    return strString;
}

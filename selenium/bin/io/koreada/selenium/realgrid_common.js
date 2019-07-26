/**
 * 
 */


/**
 * 그리드 를 관리하기 위한 전역변수
 */
var __grids = {};
var _oldFilterColumn;
RealGrids.onload = function (id){
	
	//console.log("onload !!==========>");
	if (__grids[id]){
		var gridCfg = __grids[id];
		if ("grid" == gridCfg.type) {
			gridCfg.gridView     = new RealGrids.GridView(id);
			gridCfg.dataProvider = new RealGrids.LocalDataProvider();

		} else {
			gridCfg.gridView     = new RealGrids.TreeView(id);
			gridCfg.dataProvider = new RealGrids.LocalTreeDataProvider();
		}
		gridCfg.gridView.setDataProvider(gridCfg.dataProvider);
		
		//레이어 팝업으로 그리드 생성 후 팝업 닫고하면 오류 발생 시킴
		//RealGrids.setExternalWheelEvents(gridCfg.gridView, true, false);
		
		RealGrids.enableImeOnExit(true);
		
		RgUtil.ready(gridCfg);
		
		//loadData();
	
	}
};



/**
 * 그리드 관리용 유틸
 */
var RgUtil = {
 
    /**
     * 기본 그리드 옵션객체 생성
     * @returns {}
     */
    getDefaultGridCfg : function (){
    	return {
    		type: "grid",
    		options : {
                   panel   : {visible : false},
                   footer  : {visible : false},
                   checkBar: {visible : false},
                   stateBar: {visible : false},
                   display : {rowHeight : 29, focusColor : "#d1ab63", focusBackground : "#2fffa500"},
                   header  : {height    : 32},
                   select  : {style     : "singleRow"}
               }
    	};
    },

	setup : function (id, width, height, gridCfg) {
	    if (gridCfg) {
	    	if (__grids[id]){
	    		__grids[id].options = gridCfg.options;
	    		__grids[id].columns = gridCfg.columns;
	    		__grids[id].fields  = gridCfg.fields;
	    		__grids[id].datas   = gridCfg.datas;
	    		//this.ready(__grids[id]);
	    		//return;
	    	} 
	    } else {
    		gridCfg = {};
    	}

	    __grids[id] = gridCfg;

	    __grids[id].id   = id;
		__grids[id].type = "grid";

		$("#" + id).html("");

		var html5supported = checkHtml5Browser();

		if(html5supported){
			//HTML5를 지원하여 RealGridJS를 실행합니다.

            if (!isNaN(width))
                width = width + "px";
            if (!isNaN(height))
                height = height + "px";

            $("#" + id).css({ width: width, height: height });

			//그리드에서 사용할 이미지 경로 지정
			RealGridJS.setRootContext(CONST_URL_SW + "/realgrid/RealGridJs/scripts");

			// 그리드에서 사용할 데이터프로바이터 생성
			if ("grid" == __grids[id].type) {
				__grids[id].dataProvider = new RealGridJS.LocalDataProvider();
				
				//console.log("조회 그리드 " + gridCfg["isIbkEditGrid"]);
				
				if( !is_True(__grids[id]["isIbkEditGrid"]) ) {
					//console.log("조회 그리드 ");
					__grids[id].gridView     = new RealGridJS.GridView(id, true);
				} else {
					//편집 그리드
					//console.log("편집 그리드");
					__grids[id].gridView     = new RealGridJS.GridView(id);
				}
				
				var _cellData = null;
				
				_cellData = $('<div/>').attr('id', 'cellData').attr('aria-live','assertive').attr('role','region').attr('aria-atomic','true').attr('aria-hidden','false').attr('aria-relevant','additions');
				_cellData.css('display', 'none');
				//$('body').append(_cellData);
				$('#' + id).append(_cellData);
				

			} else {
				__grids[id].dataProvider = new RealGridJS.LocalTreeDataProvider();
				__grids[id].gridView     = new RealGridJS.TreeView(id);
			}
			__grids[id].gridView.setDataSource(gridCfg.dataProvider);
			this.ready(__grids[id]);


			__grids[id].gridView.setEditorOptions({yearDisplayFormat: "{Y}년",monthDisplayFormat: "{M}월", weekDays:["일","월","화","수","목","금","토"]});
			__grids[id].gridView.setEditOptions({deleteRowsMessage: "현재 행을 삭제 하시겠습니가?"});
		} else {
			//HTML5를 지원하지 않아 RealGrid+를 실행합니다.
			var flashvars = {
					id: id
			};

			var pars = {
					quality : "high",
					//wmode   : "opaque",
					wmode   : "transparent",
					allowscriptaccess : "sameDomain",
					allowfullscreen : false,
					seamlesstabbing : false
			};

			var attrs  = {
				id: id,
				name: id,
				align: "middle",
				wmode   : "transparent"
			};

			if ("grid" == gridCfg.type){
				
				var swfUrl = "";
				//console.log("location : " + location.href);
				if (location.href.indexOf("http://localhost") == 0) {
					
					swfUrl = "/uib/sw/realgrid/RealGridPlus/objects/RealGridWeb.swf"; 
					swfUrl = swfUrl + "?" + new Date().getTime();
				} else {
					
					swfUrl = CONST_URL_SW+"/realgrid/RealGridPlus/objects/RealGridWeb.swf";
				};
				//console.log("swfUrl : " + swfUrl);
				swfobject.embedSWF(swfUrl, id, width, height, "11.1.0",CONST_URL_SW+"/realgrid/RealGridPlus/objects/expressInstall.swf", flashvars, pars, attrs,swfCallBackTest(id));

			} else {
				var swfUrl = "../RealGridPlus/objects/TreeGridWeb.swf";
				if (location.href.indexOf("http://localhost") == 0) {
					swfUrl = swfUrl + "?" + new Date().getTime();
				};
				swfobject.embedSWF(swfUrl, id, width, height, "11.1.0",CONST_URL_SW+"/realgrid/RealGridPlus/objects/expressInstall.swf", flashvars, pars, attrs,swfCallBackTest);
			}
		}
	},
	popLayerFilter : function (id){
		// 필터 설정 DIV작성
		if ($("#layer_filter_"+ id).length <= 0){
			var innerHtml = "\n";
	        innerHtml += "<div class='modal' id='layer_filter_"+ id +"'>\n";
	        innerHtml += "<div class='dvlayer'>\n";
	        innerHtml += "	<strong class='stit'>검색</strong>\n";
	        innerHtml += "	<div class='layer_cont'>\n";
	        innerHtml += "		<p class='lay_txt_cnt'>검색할 값을 입력해 주세요.</p>\n";
	        innerHtml += "		<div class='lay_input'>\n";
	        innerHtml += "			<input type='text' class='tx' style='width:100px;' id='in_filter_begin_" + id + "'/> ~\n";
	        innerHtml += "			<input type='text' class='tx' style='width:100px;' id='in_filter_end_" + id + "'/>\n";
	        innerHtml += "		</div>\n";
	        innerHtml += "		<div class='lay_btn_area'><a href='#' class='btn_lay layer_confirm' id='btn_aply_filter_"+ id + "'>검색</a></div>\n";
	        innerHtml += "	</div>\n";
	        innerHtml += "	<a href='#none' class='layer_close'><img src='" + strImgPath + "/button/layer_close.png' alt='닫기' /></a>\n";
	        innerHtml += "</div>\n";
	        innerHtml += "</div>\n";
	
	        $('body').append(innerHtml);
	
	        //검색버튼 이벤트 핸들러 설정
	    	var objSetBtn = document.getElementById("btn_aply_filter_" + id);
	    	$("#btn_aply_filter_" + id).on("click",function (){
	    		__grids[id].contextMenu[1].enabled = true;
	    		__grids[id].gridView.setContextMenu(__grids[id].contextMenu);
	    		if (window.filterInfo) {
		    	    var gridView = filterInfo.gridView;
		    		var column   = filterInfo.column;

		    		gridView.clearColumnFilters(column);

		    		var sFielterBegin = $("#in_filter_begin_" + id).val();
		    		var sFielterEnd   = $("#in_filter_end_" + id).val();

		    		//필터 정의
		    		gridView.addColumnFilters(column,[{name: "값이 '" + sFielterBegin + "' ~ '" + sFielterEnd + "'" ,
		    			                               criteria: "(value >= '" + sFielterBegin + "') and (value <= '" + sFielterEnd + "')" ,
		    			                               active: true}
		    		                                 ]);
		    		// 초기화
		    		$("#in_filter_begin_" + id).val('');
		    		$("#in_filter_end_" + id).val('');
	    	    }
	    	});
		}
    	
    	layerModalPop("layer_filter_"+ id, false, true);
	},
	popLayerDetailView : function (id, index){
		// 그리드 더블클릭 자세히보기 처리용 DIV작성
		if ($('#layer_detail_realgrid').length <= 0){
			var innerHtml = "\n";
	        innerHtml += "    <div class='modal' id='layer_detail_realgrid' style='display:none; width:550px'>";
            innerHtml += "        <div class='dvlayer'>";
            innerHtml += "            <strong class='stit'>상세보기</strong>";
            innerHtml += "            <div class='layer_cont'>";
            innerHtml += "                <div class='lay_grid_scroll_area'>";
            innerHtml += "                    <div class='TA_01 space0'>";
            innerHtml += "                        <table class='list detail' summary=''>";
            innerHtml += "                            <caption>처리 결과에 대한 상세 표</caption>";
            innerHtml += "                            <colgroup>";
            innerHtml += "                                <col style='width:35%;' />";
            innerHtml += "                                <col style='width:*;' />";
            innerHtml += "                            </colgroup>";
            innerHtml += "                            <tbody id='tbody_detail_realgrid' >";
            innerHtml += "                            </tbody>";
            innerHtml += "                        </table>";
            innerHtml += "                    </div>";
            innerHtml += "                </div>";
            innerHtml += "                <div class='lay_grid_btm_area'>";
            innerHtml += "                    <p class='txt_alone'>주의 : 본 처리결과는 변경가능성이 있어 법적 효력이 없습니다.</p>";
            innerHtml += "                    <p class='lay_grid_btm_logo'><img src='" + strImgPath + "/common/img_logo.gif' alt='IBK 기업은행' /></p>";
            innerHtml += "                </div>";
            innerHtml += "                <div class='lay_btn_area'>";
            innerHtml += "                    <a href='#' class='btn_lay' onclick=\"javascript:PTMPrintWebPage({elementid:'#layer_detail_realgrid'});\">인쇄하기</a>";
            innerHtml += "                </div>";
            innerHtml += "            </div>";
            innerHtml += "            <a href='#none' class='layer_close'><img src='" + strImgPath + "/button/layer_close.png' alt='닫기' /></a>";
            innerHtml += "        </div>";
            innerHtml += "    </div>";
            
	        $('body').append(innerHtml); 
		}
		
		// 자세히 보기 구성
		
		$('#tbody_detail_realgrid').html('');
		var colLsit = __grids[id].gridView.getDisplayColumns();
		for (var i = 0; i < colLsit.length ; i++){
			var aHeader = __grids[id].gridView.getColumnProperty(colLsit[i],"header");
			var currValue = __grids[id].gridView.getValue(index.itemIndex, colLsit[i]);
			if(currValue !=undefined && currValue != null) {
				if (currValue.getFullYear){
					$('#tbody_detail_realgrid').append("<tr><th scope='row' class='tit'>" + aHeader.text + "</th><td class='tx'>" + currValue.getFullYear() + "-" + lpad(currValue.getMonth() + 1, 2, '0') + "-" + lpad(currValue.getDate(), 2, '0') + "</td></tr>");
				} else{
				    $('#tbody_detail_realgrid').append("<tr><th scope='row' class='tit'>" + aHeader.text + "</th><td class='tx'>" + __grids[id].gridView.getValue(index.itemIndex, colLsit[i]) + "</td></tr>"); 
				}
			} else {
				$('#tbody_detail_realgrid').append("<tr><th scope='row' class='tit'>" + aHeader.text + "</th><td class='tx'></td></tr>");
			}
		}

		layerModalPop('layer_detail_realgrid', false, true);
		//layerModalPop
    
	},
	popLayerPrint : function (id){
		// 인쇄 설정 DIV작성
		if ($("#layer_print_"+ id).length <= 0){
			var innerHtml = "\n";
	        innerHtml += "<div class='modal' id='layer_print_"+ id +"'>\n";
	        innerHtml += "    <div class='dvlayer area2'>\n";
	        innerHtml += "        <strong class='stit'>PDF저장설정</strong>\n";
	        innerHtml += "        <div class='layer_cont'>\n";
	        innerHtml += "            <div class='ly_tit_area'>\n";
	        innerHtml += "                <strong class='ly_cnt_tit etc'>\n";
	        innerHtml += "                    <label for='user_ly_chk1'>항목설정</label> <input type='checkbox' class='checkbox' id='user_ly_chk1' />\n";
	        innerHtml += "                    <label for='user_ly_chk2'>항목설정 저장</label> <input type='checkbox' class='checkbox' id='user_ly_chk2' />\n";
	        innerHtml += "                </strong>\n";
	        innerHtml += "                <div class='ly_right_box'>\n";
	        innerHtml += "                    <a href='#' class='btn_white approval' id='click_layer_approval_line_" + id + "' onclick=\"javascript:RgUtil.popLayerApprovalLine('" + id +"');\" ><span>결재란설정</span></a>\n";
	        innerHtml += "                    <a href='#' class='btn_white print layer_confirm' id='btn_save_pdf_" + id + "'><span>PDF저장</span></a>\n";
	        innerHtml += "                </div>\n";
	        innerHtml += "            </div>\n";
	        innerHtml += "            <div class='data_type_box'>\n";
	        innerHtml += "                <dl>\n";
	        innerHtml += "                    <dt><label for='user_ly_num1'>제목 (최대 14자)</label></dt>\n";
	        innerHtml += "                    <dd><input type='text' id='user_ly_num1' class='tx' /></dd>\n";
	        innerHtml += "                    <dt><label for='user_ly_num2'>인쇄할 폰트(Font) 지정</label></dt>\n";
	        innerHtml += "                    <dd>\n";
	        innerHtml += "                        <select id='user_ly_num2'>\n";
	        innerHtml += "                            <option value=''>1</option>\n";
	        innerHtml += "                            <option value=''>2</option>\n";
	        innerHtml += "                            <option value=''>3</option>\n";
	        innerHtml += "                            <option value=''>4</option>\n";
	        innerHtml += "                        </select>\n";
	        innerHtml += "                    </dd>\n";
	        innerHtml += "                    <dt>인쇄방향</dt>\n";
	        innerHtml += "                    <dd>\n";
	        innerHtml += "                        <input type='radio' class='radio' name='user_ly_chkt' id='user_ly_chk01' />\n";
	        innerHtml += "                        <label for='user_ly_chk01'>세로</label> &nbsp;\n";
	        innerHtml += "                        <input type='radio' class='radio' name='user_ly_chkt' id='user_ly_chk02' />\n";
	        innerHtml += "                        <label for='user_ly_chk02'>가로</label>\n";
	        innerHtml += "                    </dd>\n";
	        innerHtml += "                </dl>\n";
	        innerHtml += "                <dl class='admin_process_wrap'>\n";
	        innerHtml += "                    <dt>원하는 인쇄 항목을 <strong>오른쪽</strong>으로 이동 하세요.</dt>\n";
	        innerHtml += "                    <dd>\n";
	        innerHtml += "                        <div class='admin_process'>\n";
	        innerHtml += "                            <div class='left'>\n";
	        innerHtml += "                                <strong class='tit'>인쇄 가능한 항목</strong>\n";
	        innerHtml += "                                <ul class='list'>\n";
	        innerHtml += "                                    <li class='active'><a href='#'>입금계좌번호</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>입금계좌 예금주</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>입금은행</a></li>\n";
	        innerHtml += "                                </ul>\n";
	        innerHtml += "                            </div>\n";
	        innerHtml += "                            <div class='btn_ctr'>\n";
	        innerHtml += "                                <a href='#' class='btn_blue bt_md arrowb_r'><span>추가</span></a>\n";
	        innerHtml += "                                <a href='#' class='btn_white bt_md arroww_l'><span>제거</span></a>\n";
	        innerHtml += "                            </div>\n";
	        innerHtml += "                            <div class='right'>\n";
	        innerHtml += "                                <strong class='tit'>인쇄 항목</strong>\n";
	        innerHtml += "                                <ul class='list'>\n";
	        innerHtml += "                                    <li><a href='#'>처리결과</a></li>\n";
	        innerHtml += "                                    <li class='active'><a href='#'>이체일시</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>출금계좌번호</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>이체금액</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>내통장에 표시한 내용</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>처리결과</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>이체일시</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>출금계좌번호</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>이체금액</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>내통장에 표시한 내용</a></li>\n";
	        innerHtml += "                                </ul>\n";
	        innerHtml += "                            </div>\n";
	        innerHtml += "                        </div>\n";
	        innerHtml += "                    </dd>\n";
	        innerHtml += "                </dl>\n";
	        innerHtml += "            </div>\n";
	        innerHtml += "            <strong class='ly_cnt_tit'>인쇄항목</strong>\n";
	        innerHtml += "            <div class='ly_grid_area' id='ly_grid_"+ id +"_print' style='width:100%; height:150px;'></div>\n";
	        innerHtml += "        </div>\n";
	        innerHtml += "        <a href='#none' class='closeModalLayer layer_close'><img src='" + strImgPath + "/button/layer_close.png' alt='닫기' /></a>\n";
	        innerHtml += "    </div>\n";
	        innerHtml += "</div>\n";
	
	        $('body').append(innerHtml);
	        
	        //검색버튼 이벤트 핸들러 설정
	        
	        $('#table_detail_realgrid').html('');
	        /*
	    	var objSetBtn = document.getElementById("btn_aply_filter_" + id);
	    	if (objSetBtn){
	    		objSetBtn.onclick = function (){
		    		__grids[id].contextMenu[1].enabled = true;
		    		__grids[id].gridView.setContextMenu(__grids[id].contextMenu);
		    		if (window.filterInfo) {
			    	    var gridView = filterInfo.gridView;
			    		var column   = filterInfo.column;
	
			    		gridView.clearColumnFilters(column);
	
			    		var sFielterBegin = document.getElementById("in_filter_" + id + "_begin").value;
			    		var sFielterEnd   = document.getElementById("in_filter_" + id + "_end").value;
	
			    		//필터 정의
			    		gridView.addColumnFilters(column,[{name: "값이 '" + sFielterBegin + "' ~ '" + sFielterEnd + "'" ,
			    			                               criteria: "(value >= '" + sFielterBegin + "') and (value <= '" + sFielterEnd + "')" ,
			    			                               active: true}
			    		                                 ]);
		    	    }
	    		};
	    	}
	    	*/
    	}
    	
    	layerModalPop("layer_print_"+ id, false, true);
	},
	popLayerSaveFile : function (id){
		//파일저장항목설정 DIV작성
		if ($("#layer_save_file_"+ id).length <= 0){
			var innerHtml = "\n";
	        innerHtml += "<div class='modal' id='layer_save_file_"+ id +"'>\n"; 
	        innerHtml += "    <div class='dvlayer area2'>\n";
	        innerHtml += "        <strong class='stit'>파일저장항목설정</strong>\n";
	        innerHtml += "        <div class='layer_cont layer_file'>\n";
	        innerHtml += "            <div class='ly_tit_area'>\n";
	        innerHtml += "                <strong class='ly_cnt_tit etc'><label for='user_ly_chk03'>항목설정</label> <input type='checkbox' class='checkbox' id='user_ly_chk03' /></strong>\n";
	        innerHtml += "                <div class='ly_right_box'>\n";
	        innerHtml += "                    <a href='#' class='btn_white excel_save layer_confirm'><span>엑셀파일저장</span></a>\n";
	        innerHtml += "                    <a href='#' class='btn_white tx_save layer_confirm'><span>텍스트파일저장</span></a>\n";
	        innerHtml += "                    <a href='#' class='btn_white column layer_confirm'><span>컬럼별표준포맷저장</span></a>\n";
	        innerHtml += "                </div>\n";
	        innerHtml += "            </div>\n";
	        innerHtml += "            <div class='data_type_box'>\n";
	        innerHtml += "                <dl class='admin_process_wrap'>\n";
	        innerHtml += "                    <dt>원하는 인쇄 항목을 <strong>오른쪽</strong>으로 이동 하세요.</dt>\n";
	        innerHtml += "                    <dd>\n";
	        innerHtml += "                        <div class='admin_process'>\n";
	        innerHtml += "                            <div class='left'>\n";
	        innerHtml += "                                <strong class='tit'>인쇄 가능한 항목</strong>\n";
	        innerHtml += "                                <ul class='list'>\n";
	        innerHtml += "                                    <li class='active'><a href='#'>입금계좌번호</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>입금계좌 예금주</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>입금은행</a></li>\n";
	        innerHtml += "                                </ul>\n";
	        innerHtml += "                            </div>\n";
	        innerHtml += "                            <div class='btn_ctr'>\n";
	        innerHtml += "                                <a href='#' class='btn_blue bt_md arrowb_r'><span>추가</span></a>\n";
	        innerHtml += "                                <a href='#' class='btn_white bt_md arroww_l'><span>제거</span></a>\n";
	        innerHtml += "                            </div>\n";
	        innerHtml += "                            <div class='right'>\n";
	        innerHtml += "                                <strong class='tit'>인쇄 항목</strong>\n";
	        innerHtml += "                                <ul class='list'>\n";
	        innerHtml += "                                    <li><a href='#'>처리결과</a></li>\n";
	        innerHtml += "                                    <li class='active'><a href='#'>이체일시</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>출금계좌번호</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>이체금액</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>내통장에 표시한 내용</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>처리결과</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>이체일시</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>출금계좌번호</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>이체금액</a></li>\n";
	        innerHtml += "                                    <li><a href='#'>내통장에 표시한 내용</a></li>\n";
	        innerHtml += "                                </ul>\n";
	        innerHtml += "                            </div>\n";
	        innerHtml += "                        </div>\n";
	        innerHtml += "                    </dd>\n";
	        innerHtml += "                </dl>\n";
	        innerHtml += "            </div>\n";
	        innerHtml += "            <strong class='ly_cnt_tit'>저장할내용</strong>\n";
	        innerHtml += "            <div class='ly_grid_area' id='ly_grid_"+ id +"_file' style='width:100%; height:150px;'></div>\n";
	        innerHtml += "        </div>\n";
	        innerHtml += "        <a href='#none' class='closeModalLayer layer_close'><img src='" + strImgPath + "/button/layer_close.png' alt='닫기' /></a>\n";
	        innerHtml += "    </div>\n";
	        innerHtml += "</div>\n";
	
	        $('body').append(innerHtml);
	
	        //검색버튼 이벤트 핸들러 설정
	        /*
	    	var objSetBtn = document.getElementById("btn_aply_filter_" + id);
	    	if (objSetBtn){
	    		objSetBtn.onclick = function (){
		    		__grids[id].contextMenu[1].enabled = true;
		    		__grids[id].gridView.setContextMenu(__grids[id].contextMenu);
		    		if (window.filterInfo) {
			    	    var gridView = filterInfo.gridView;
			    		var column   = filterInfo.column;
	
			    		gridView.clearColumnFilters(column);
	
			    		var sFielterBegin = document.getElementById("in_filter_" + id + "_begin").value;
			    		var sFielterEnd   = document.getElementById("in_filter_" + id + "_end").value;
	
			    		//필터 정의
			    		gridView.addColumnFilters(column,[{name: "값이 '" + sFielterBegin + "' ~ '" + sFielterEnd + "'" ,
			    			                               criteria: "(value >= '" + sFielterBegin + "') and (value <= '" + sFielterEnd + "')" ,
			    			                               active: true}
			    		                                 ]);
		    	    }
	    		};
	    	}
    	*/
		}
		
		layerModalPop("layer_save_file_"+ id, false, true);
	},	
	popLayerApprovalLine : function (id){
		// 그리드 결재선 처리용 DIV작성
		if ($("#layer_approval_line_"+ id).length <= 0){
			var innerHtml = "\n";
	        
	        innerHtml += "<div class='modal' id='layer_approval_line_"+ id +"' style='display:none; width:1005px'>";
            innerHtml += "	<div class='dvlayer'>";
            innerHtml += "		<strong class='stit'>결재란 설정</strong>";
            innerHtml += "		<div class='layer_cont'>";
            innerHtml += "			<div class='lay_payment_area'>";
            innerHtml += "				<div class='payment_box payment_left_box'>";
            innerHtml += "					<ol>";
            innerHtml += "						<li>";
            innerHtml += "							<ul>";
            innerHtml += "								<li class='payment_position'><label for='position01' class='screen_out'>직책</label><input type='text' id='position01' value='직책' title='직책을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "								<li class='payment_decide'><input type='checkbox' id='decide01' /> <label for='decide01'>전결</label></li>";
            innerHtml += "								<li class='payment_approval'><label for='approval01' class='screen_out'>결재자명</label><input type='text' id='approval01' value='결재자명' title='결재자명을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "							</ul>";
            innerHtml += "						</li>";
            innerHtml += "						<li>";
            innerHtml += "							<ul>";
            innerHtml += "								<li class='payment_position'><label for='position02' class='screen_out'>직책</label><input type='text' id='position02' value='직책' title='직책을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "								<li class='payment_decide'><input type='checkbox' id='decide02' /> <label for='decide02'>전결</label></li>";
            innerHtml += "								<li class='payment_approval'><label for='approval02' class='screen_out'>결재자명</label><input type='text' id='approval02' value='결재자명' title='결재자명을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "							</ul>";
            innerHtml += "						</li>";
            innerHtml += "						<li>";
            innerHtml += "							<ul>";
            innerHtml += "								<li class='payment_position'><label for='position03' class='screen_out'>직책</label><input type='text' id='position03' value='직책' title='직책을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "								<li class='payment_decide'><input type='checkbox' id='decide03' /> <label for='decide03'>전결</label></li>";
            innerHtml += "								<li class='payment_approval'><label for='approval03' class='screen_out'>결재자명</label><input type='text' id='approval03' value='결재자명' title='결재자명을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "							</ul>";
            innerHtml += "						</li>";
            innerHtml += "						<li>";
            innerHtml += "							<ul>";
            innerHtml += "								<li class='payment_position'><label for='position04' class='screen_out'>직책</label><input type='text' id='position04' value='직책' title='직책을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "								<li class='payment_decide'><input type='checkbox' id='decide04' /> <label for='decide04'>전결</label></li>";
            innerHtml += "								<li class='payment_approval'><label for='approval04' class='screen_out'>결재자명</label><input type='text' id='approval04' value='결재자명' title='결재자명을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "							</ul>";
            innerHtml += "						</li>";
            innerHtml += "						<li>";
            innerHtml += "							<ul>";
            innerHtml += "								<li class='payment_position'><label for='position05' class='screen_out'>직책</label><input type='text' id='position05' value='직책' title='직책을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "								<li class='payment_decide'><input type='checkbox' id='decide05' /> <label for='decide05'>전결</label></li>";
            innerHtml += "								<li class='payment_approval'><label for='approval05' class='screen_out'>결재자명</label><input type='text' id='approval05' value='결재자명' title='결재자명을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "							</ul>";
            innerHtml += "						</li>";
            innerHtml += "					</ol>";
            innerHtml += "				</div>";
            innerHtml += "				<div class='payment_box payment_right_box'>";
            innerHtml += "					<ol>";
            innerHtml += "						<li>";
            innerHtml += "							<ul>";
            innerHtml += "								<li class='payment_position'><label for='position06' class='screen_out'>직책</label><input type='text' id='position06' value='직책' title='직책을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "								<li class='payment_decide'><input type='checkbox' id='decide06' /> <label for='decide06'>전결</label></li>";
            innerHtml += "								<li class='payment_approval'><label for='approval06' class='screen_out'>결재자명</label><input type='text' id='approval06' value='결재자명' title='결재자명을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "							</ul>";
            innerHtml += "						</li>";
            innerHtml += "						<li>";
            innerHtml += "							<ul>";
            innerHtml += "								<li class='payment_position'><label for='position07' class='screen_out'>직책</label><input type='text' id='position07' value='직책' title='직책을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "								<li class='payment_decide'><input type='checkbox' id='decide07' /> <label for='decide07'>전결</label></li>";
            innerHtml += "								<li class='payment_approval'><label for='approval07' class='screen_out'>결재자명</label><input type='text' id='approval07' value='결재자명' title='결재자명을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "							</ul>";
            innerHtml += "						</li>";
            innerHtml += "						<li>";
            innerHtml += "							<ul>";
            innerHtml += "								<li class='payment_position'><label for='position08' class='screen_out'>직책</label><input type='text' id='position08' value='직책' title='직책을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "								<li class='payment_decide'><input type='checkbox' id='decide08' /> <label for='decide08'>전결</label></li>";
            innerHtml += "								<li class='payment_approval'><label for='approval08' class='screen_out'>결재자명</label><input type='text' id='approval08' value='결재자명' title='결재자명을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "							</ul>";
            innerHtml += "						</li>";
            innerHtml += "						<li>";
            innerHtml += "							<ul>";
            innerHtml += "								<li class='payment_position'><label for='position09' class='screen_out'>직책</label><input type='text' id='position09' value='직책' title='직책을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "								<li class='payment_decide'><input type='checkbox' id='decide09' /> <label for='decide09'>전결</label></li>";
            innerHtml += "								<li class='payment_approval'><label for='approval09' class='screen_out'>결재자명</label><input type='text' id='approval09' value='결재자명' title='결재자명을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "							</ul>";
            innerHtml += "						</li>";
            innerHtml += "						<li>";
            innerHtml += "							<ul>";
            innerHtml += "								<li class='payment_position'><label for='position10' class='screen_out'>직책</label><input type='text' id='position10' value='직책' title='직책을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "								<li class='payment_decide'><input type='checkbox' id='decide10' /> <label for='decide10'>전결</label></li>";
            innerHtml += "								<li class='payment_approval'><label for='approval10' class='screen_out'>결재자명</label><input type='text' id='approval10' value='결재자명' title='결재자명을 최대 5글자로 입력해주세요.' maxlength='5' /></li>";
            innerHtml += "							</ul>";
            innerHtml += "						</li>";
            innerHtml += "					</ol>";
            innerHtml += "				</div>";
            innerHtml += "			</div>";
            innerHtml += "			<div class='lay_btn_area'>";
            innerHtml += "				<a href='#click_layer_approval_line_" + id + "' class='btn_lay layer_confirm'>확인</a>";
            innerHtml += "			</div>";
            innerHtml += "		</div>";
            innerHtml += "		<a href='#click_layer_approval_line_" + id + "' class='layer_close'><img src='" + strImgPath + "/button/layer_close.png' alt='닫기' /></a>";
            innerHtml += "	</div>";
            innerHtml += "</div>";	        
	        
	        $('body').append(innerHtml); 
	
	        //검색버튼 이벤트 핸들러 설정
	        /*
	    	var objSetBtn = document.getElementById("btn_aply_filter_" + id);
	    	if (objSetBtn){
	    		objSetBtn.onclick = function (){
		    		__grids[id].contextMenu[1].enabled = true;
		    		__grids[id].gridView.setContextMenu(__grids[id].contextMenu);
		    		if (window.filterInfo) {
			    	    var gridView = filterInfo.gridView;
			    		var column   = filterInfo.column;
	
			    		gridView.clearColumnFilters(column);
	
			    		var sFielterBegin = document.getElementById("in_filter_" + id + "_begin").value;
			    		var sFielterEnd   = document.getElementById("in_filter_" + id + "_end").value;
	
			    		//필터 정의
			    		gridView.addColumnFilters(column,[{name: "값이 '" + sFielterBegin + "' ~ '" + sFielterEnd + "'" ,
			    			                               criteria: "(value >= '" + sFielterBegin + "') and (value <= '" + sFielterEnd + "')" ,
			    			                               active: true}
			    		                                 ]);
		    	    }
	    		};
	    	}
	    	*/
		}
		
		layerModalPop("layer_approval_line_"+ id, false, true);
	},	
	/**  
	 * 그리드 사전작업 완료후 화면에 표시
	 * @param gridCfg
	 */
    ready : function (gridCfg) {
    	if (gridCfg.options) {
    		gridCfg.gridView.setOptions(gridCfg.options);
    	}

    	if (gridCfg.columns) {
    		gridCfg.gridView.setColumns(gridCfg.columns);
    	}

    	if (gridCfg.fields) {
    		gridCfg.dataProvider.setFields(gridCfg.fields);
    	}

    	if (gridCfg.datas) {
    		gridCfg.dataProvider.setRows(gridCfg.datas);
    	}
    	
    	// 그리드 기본 스킨 적용
    	gridCfg.gridView.setStyles(skin_ibk_default);

    	//팝업메뉴 연결
    	this.setPopupMenu(gridCfg.id);
    	
    	//엔터키 입력시 다음 셀로 이동 안되게 하는 코드
    	if(!is_True(gridCfg["isIbkEditGrid"]) ) {
	    	gridCfg.gridView.setEditOptions({
	    	    enterToTab: false,
	    	    enterToEdit : true
	    	});
    	}

    	//편집시 웹표준화 적용
    	if(is_True(gridCfg["isIbkEditGrid"]) && checkHtml5Browser() ) {
    		gridCfg.gridView.setEditorOptions({titleStatement:"${columnheader}을 입력하세요."}); 
    	}
    	
    	//탭이동시 다음 레코드 이동 가능하도록
    	gridCfg.gridView.setEditOptions({
    	    crossWhenExitLast: true,
    	    //웹접근성 관련 추가
    	    checkWhenSpace: true,
    	    exitGridWhenTab: "grid"
    	});
    	
    	gridCfg.gridView.setIbkGridId(gridCfg.id);
    	
        //console.log("ibkGridTitle property ok" + gridCfg["ibkGridTitle"]);
        
        if(!is_NullValue(gridCfg["ibkGridTitle"])) {
        	//console.log("ibkGridTitle property ok" + gridCfg["ibkGridTitle"]);
        	
        	gridCfg.gridView.setGridTitle(gridCfg["ibkGridTitle"]); 
        }

		// 필터처리를 위한 DIV생성
        //this.setLayerFilter(gridCfg.id); 
        
        // 그리드 더블클릭시 상세보기창
        //this.setLayerDetailView(gridCfg.id);
        
		// 그리드 인쇄를 위한 DIV생성
        //this.setLayerPrint(gridCfg.id);
        
		// 그리드 파일저장을 위한 DIV생성
        //this.setLayerSaveFile(gridCfg.id);
        
        // 그리드 인쇄시 결재선 처리를 위한 DIV생성
        //this.setLayerApprovalLine(gridCfg.id);
    	
    	
    	if(checkHtml5Browser() && !is_True(gridCfg["isIbkEditGrid"]) ) {
    		//console.log("JS, and isIbkEditGrid false, and 조회 그리드");
    		gridCfg.gridView.onCurrentChanged = function (grid, newIndex) {
    			//console.log("onCurrentChanged grid" + grid);
    		    //var colHeader = gridView.getColumnProperty(newIndex.column, "header");
    			var colHeader = grid.getColumnProperty(newIndex.column, "header");
    		    var title = colHeader && colHeader.text ? colHeader.text : newIndex.column;
    		    var v = title + "열 " + newIndex.itemIndex + "행 " + grid.getValue(newIndex.itemIndex, newIndex.fieldName);
    		    $("#cellData").text(v);

    		};

    	}
    	
    	if(gridCfg.customRowDynamicStyles) {
    		//gridCfg.option.body.dynamicStyles
    		//var body =gridCfg.gridView.getStyles("body",true) || {};
    		//body.dynamicStyles = gridCfg.bodyDynamicStylesIbk;
    		
    		var styleConfigs = {
    				body : {
    					dynamicStyles : gridCfg.bodyDynamicStylesIbk
    				}
    		};
    		
    		//console.log("body.dynamicStyles : " + JSON.stringify(body.dynamicStyles));
    		
    		//console.log("body : " + JSON.stringify(styleConfigs));
    		
    		gridCfg.gridView.setStyles(styleConfigs);
    		
    	}
    	
       	// 그리드 보기저장 적용
    	try {
	        if( !is_NullValue(localStorage) ) {
	        	this.saveOriginalGridView(gridCfg.gridView);
	        	
	        	var gridColumnInfo	= this.getGridViewConfigration(gridCfg.gridView);
	        	if( !is_NullValue(gridColumnInfo) ) {
	        		gridColumnInfo	= JSON.parse(gridColumnInfo);
	        		
		        	var columns		= new Array();
		        	var columnNames	= gridCfg.gridView.getColumnNames(true);
		        	$.each(columnNames, function(i, col) {
		        		var column	= gridCfg.gridView.columnByName(col);
		        		
		        		if( !is_NullValue(gridColumnInfo[col]) ) {
		        			column.visible		= gridColumnInfo[col].visible;
		        			column.displayIndex	= gridColumnInfo[col].displayIndex;
		        		}
	
		        		gridCfg.gridView.setColumn(column);
		        	});
	        	}
	        }
    	} catch(e) {}
    	
    	if( !is_NullValue(gridCfg.customColumnEvents) ) {
    		this.setColumnInputTypes(gridCfg.id, gridCfg.customColumnEvents);
    	}
        
        gridCfg.gridView.onDataCellDblClicked = function (grid, index) {
        	//console.log("onDataCellDblClicked " + grid.getIbkGridId());
        	
        	//셀내 버튼이 클릭되는 경우 skip
        	if( gridCfg.gridView.getColumnProperty(gridCfg.gridView.getCurrent().column,"button") === "image" ) {
        		//console.log("imageButton cell db click!!");
        		return;
        	}
        	
        	// 편집모드가 아닌 경우만 팝업 호출
        	if( !gridCfg.gridView.getColumnProperty(gridCfg.gridView.getCurrent().column,"editable") ) {
        		//RgUtil.popLayerDetailView(gridCfg.id, index);
        		var url	= "";
        		if (location.href.indexOf("localhost") >= 0) {
        			url = "http://localhost:8080/uib/jsp/common/comm_gridDetailView.jsp";
        		} else {
        			url = baseCtxPath + "/jsp/common/comm_gridDetailView.jsp";
        			//url = "../jsp/common/comm_gridDetailView.jsp";
        		}
        		var param = "gridId=" + grid.getIbkGridId();
    			open_ajax(url, param, function(){}, true);
        	}
        }
        
       	// F1 키 정렬기능 이벤트 처리
        if( checkHtml5Browser() ) {
        	
        	gridCfg.gridView.onKeyUp = function(gridObj, key, ctrl, shift, alt) {
        		//console.log(key + " : " + shift);
        		// F3
        		if( key == "114" ) {
		        	var currField	= gridObj.getCurrent().fieldName;
		        	var fields		= [];
		        	var directions	= [];
		        	if( currField ) {
		        		var sortedFields	= gridObj.getSortedFields();
		        		if( sortedFields ) {
		        			var findIndex	= sortedFields.map(function (x) { return x.orgFieldName; }).indexOf(currField); 
		        			if( findIndex > -1 ) {
		        				if( sortedFields[findIndex].direction == "ascending" ) {
		        					sortedFields[findIndex].direction = "descending";
		        				} else if( sortedFields[findIndex].direction == "descending" ) {
		        					sortedFields.splice(findIndex, 1);
		        				};
		        			} else {
		        				sortedFields.push({
		        					direction: "ascending",
		        					orgFieldName: currField
		        				});
		        			}
		
		        			for( var i = 0 ; i < sortedFields.length ; i++ ) {
		        				fields.push(sortedFields[i].orgFieldName);
		        				directions.push(sortedFields[i].direction);
		        			}
		        		} else {
		        			fields		= [currField];
		        			directions	= ["ascending"];
		        		}
		
		        		gridObj.orderBy(fields, directions);
		        	}
        		}
        		// Shift + Enter시 상세보기 클리, 위의 onDataCellDblClicked 이벤트 정의 코드 복사
        		if( key == "13" && shift ) {
        			
        			if( gridCfg.gridView.getColumnProperty(gridCfg.gridView.getCurrent().column,"button") === "image" ) {
                		//console.log("imageButton cell db click!!");
                		return;
                	}
                	
                	// 편집모드가 아닌 경우만 팝업 호출
                	if( !gridCfg.gridView.getColumnProperty(gridCfg.gridView.getCurrent().column,"editable") ) {
                		//RgUtil.popLayerDetailView(gridCfg.id, index);
                		var url	= "";
                		if (location.href.indexOf("localhost") >= 0) {
                			url = "http://localhost:8080/uib/jsp/common/comm_gridDetailView.jsp";
                		} else {
                			url = baseCtxPath + "/jsp/common/comm_gridDetailView.jsp";
                			//url = "../jsp/common/comm_gridDetailView.jsp";
                		}
                		var param = "gridId=" + gridObj.getIbkGridId();
            			open_ajax(url, param, function(){}, true);
                	}
        		}
        	}
        }
        
        // checkBar 체크 이벤트 처리
        gridCfg.gridView.onItemChecked = function (gridObj, itemIndex, checked) {
        	var index	= { itemIndex : itemIndex };
        	gridObj.setCurrent(index);
        }
        
        if(gridCfg.eventOverRideCallBack) {
        	//console.log("grid onCompleted Event !! =================================>");
        	//gridCfg.onCompleted();
        	var func = gridCfg.eventOverRideCallBack;
        	func();
        }
        
        
        if(gridCfg.onCompleted) {
        	//console.log("grid onCompleted Event !! =================================>");
        	//gridCfg.onCompleted();
        	var func = gridCfg.onCompleted;
        	func();
        }
        
        //그리드가 있는 경우 전체인쇄 막음
        //$(".printc:eq(0)").hide();
        
    },


    /**
     * 컨텍스트 메뉴 설정및 콜백
     * @param id
     */
    setPopupMenu : function (id) { 
    	if (__grids[id].gridView){
    		var gridView = __grids[id].gridView;
    		__grids[id].contextMenu = [
    		                           {label:"검색"   , tag: "ITM_FIND"        , enabled: true },
    		                           {label:"검색취소", tag: "ITM_CNL_FIND" , enabled: false},
    		                           {label:"-"},
    		                           {label:"숨기기"   , tag: "ITM_HIDE"        , enabled: true},
    		                           {label:"숨기기취소", tag: "ITM_CNL_HIDE" , enabled: false},
    		                           {label:"-"},
    		                           {label:"보기저장"  , tag: "ITM_SAVE_VIEW" , enabled: true},
    		                           {label:"초기설정복구", tag: "ITM_RESTORE_VIEW"   , enabled: true}
    		                           //{label:"-"}
    		                           //{label:"엑셀저장"  , tag: "ITM_SAVE_EXCEL" , enabled: true},
    		                           //{label:"PDF로저장", tag: "ITM_SAVE_PDF"   , enabled: true},
    		                           //{label:"인쇄"    , tag: "ITM_PRINT"      , enabled: true}
	    		                      ];

    		gridView.setContextMenu(__grids[id].contextMenu);

    		gridView.onContextMenuItemClicked = function(grid, label, index) {
    			//js는 객체 flash는 string lable
    			var command = "";
    			if(checkHtml5Browser()) {
    				command = label.label;
    			} else {
    				command = label;
    			}
    			if ("숨기기" == command){
    				__grids[id].contextMenu[4].enabled = true;
    	    		gridView.setContextMenu(__grids[id].contextMenu);
    	    		var column = grid.columnByName(index.column);
    	    		if (column){
        	    		column.visible = false;
    	    			grid.setColumn(column);
    	    		}
    			} else if ("숨기기취소" == command) {
    				__grids[id].contextMenu[4].enabled = false;
    				gridView.setContextMenu(__grids[id].contextMenu);
    	        	if (__grids[id].columns) {
    	        		__grids[id].gridView.setColumns(__grids[id].columns);
    	        	}
    			} else if ("검색" == command) {
    	    		var column = grid.columnByName(index.column);
    	    		if (column){
	    		    	window.filterInfo = {};
	    		    	filterInfo.gridView = gridView;
	    		    	filterInfo.column   = column;
	    		    	
	    		    	var url		= baseCtxPath + "/jsp/common/comm_gridPopupSearch.jsp?gridId=" + id;
	    		    	open_ajax(url, null, null, true, null, null, 0, 500);
	    		    	
    	    		}

    			} else if ("검색취소" == command) {
       				__grids[id].contextMenu[1].enabled = false;
    				gridView.setContextMenu(__grids[id].contextMenu);
    				var aColumnNms = gridView.getColumnNames(true);
    				for (var i= 0; i < aColumnNms.length ; i ++){
        				gridView.clearColumnFilters(aColumnNms[i]);
    				}
    			} else if ("ITM_SAVE_EXCEL" == label.tag) {
    				RgUtil.saveToFile(id);
    			} else if ("ITM_SAVE_PDF" == label.tag) {
    				RgUtil.saveToFilePDF(id);
    			} else if ("ITM_PRINT" == label.tag) {
    				RgUtil.showPrint(id);
    			} else if ("보기저장" == command) {
    				RgUtil.saveGridViewConfigration(id);
    			} else if ("초기설정복구" == command) {
    				RgUtil.clearGridViewConfigration(id);
    			}
    		};
    	}
    },

    /**
     * 엑셀파일로 저장
     * @param id
     */
    saveToFile: function (id) {
    	//pcs_js_common.jsp 에 기구현된 함수는 제거
    	if (__grids[id]) {
    		var gridView = __grids[id].gridView;
    		gridView.exportGrid({
    			type     : "excel",
    			target   : "local",
    			fileName : "그리드샘플.xlsx",
    			//indicator: "",
    			//header   : "",
    			//footer   : "",
    			compatibility : true
    		});
    	} else {
    		alert("그리드가 존재 하지 않습니다.");
    	}
    },

    /**
     * 그리드 본문을 인쇄한다.
     * @param id
     */
    showPrint: function (id) {
    	//pcs_js_common.jsp 에 기구현된 함수는 제거
    	//alert("구현예정입니다.");
    	uf_ShowPrintXgrid();
    },

    /**
     * PDF파일로 저장
     * @param id
     */
    saveToFilePDF : function (id) {
    	// 신규 구현이 필요함
    	alert("구현예정입니다.");
    },
    
    exportGridHtml : function(id, containerId, callback) {
    	if( checkHtml5Browser() ){
    		var container	= $("#"+containerId); 
    		container.empty();
            var gridView	= __grids[id].gridView;
            gridView.exportGrid({
                type		: "html",
                htmlType	: "text",
                checkBar	: false,
                done		: function (data) {
			                  	$(container).html(data);                    
			                    $(container).show();
			                    if( !is_NullValue(callback) ) {
			                    	callback();
			                    }
                			  }
            });
            
    	} else {
    		var container	= $("#"+containerId); 
    		container.empty();
            var gridView	= __grids[id].gridView;
            gridView.exportGrid({
                type		: "html",
                htmlType	: "text",
                checkBar	: false,
                done		: function (data) {
			                    $(container).html(data);                    
			                    $(container).show();
			                    if( !is_NullValue(callback) ) {
			                    	callback();
			                    }
			                  }
	            });
    	}
    },
    
    saveGridViewConfigration : function(id) {
    	try {
	    	if( is_NullValue(localStorage) ) {
	    		alert("보기설정을 지원하지 않는 브라우져입니다.");
	    		return;
	    	}
	
	    	var gridView 	= __grids[id].gridView;
	    	var columnNames	= gridView.getColumnNames(true);
	    	
	    	var gridViewConfigration	= {};
	    	$.each(columnNames, function(i, col) {
	    		var column			= gridView.columnByName(col);
	    		
	    		var name			= column.name;
	    		var visible			= column.visible;
	    		var displayIndex	= column.displayIndex;
	    		gridViewConfigration[name]	= { visible	  	 : visible,
	    										displayIndex : displayIndex
	    							  		  };
	    	});
	
	    	var localKey	= this.getLocalStorageKey(gridView);	    	
			localStorage.setItem(localKey, JSON.stringify(gridViewConfigration));
    	} catch(e) {}
    },
    
    getGridViewConfigration : function(gridView) {
    	try {
	    	var localKey	= this.getLocalStorageKey(gridView);
	    	return localStorage.getItem(localKey);
    	} catch(e) {
    		return null;
    	}
    },
    
    clearGridViewConfigration : function(id) {
    	try {
	    	if( is_NullValue(localStorage) ) {
	    		alert("보기설정을 지원하지 않는 브라우져입니다.");
	    		return;
	    	}
	    	
	    	var gridView  	= __grids[id].gridView;
	    	var localKey	= this.getLocalStorageKey(gridView);
	    	localStorage.removeItem(localKey);
	    	
	    	var originalGridViewConfigration	= gridView.getOriginalGridViewConfigration();
	    	var columnNames			= gridView.getColumnNames(true);
	    	$.each(columnNames, function(i, col) {
	    		var column			= gridView.columnByName(col);
	    		column.visible		= originalGridViewConfigration[col].visible;
	    		column.displayIndex	= originalGridViewConfigration[col].displayIndex;
	    		
	    		gridView.setColumn(column);
	    	});
    	} catch(e) {}
    },
    
    saveOriginalGridView : function(gridView) {
    	var columns		= {};
    	var columnNames	= gridView.getColumnNames(true);
    	$.each(columnNames, function(i, col) {
    		var column		= gridView.columnByName(col);
    		columns[col]	= { name		 : col, 
    							visible 	 : column.visible, 
    							displayIndex : column.displayIndex };
    	});
    	
    	gridView.setOriginalGridViewConfigration(columns);
    },
    
    getLocalStorageKey : function(gridView) {
    	var localKey	= "";
    	var gridTitle	= gridView.getGridTitle();
    	if( is_NullValue(gridTitle) ) {
    		gridTitle	= $(".stit3").text();
        	if( is_NullValue(gridTitle) ) {
        		gridTitle	= document.location.pathname.split("/").pop().split(".")[0];
        	}
    	}

    	localKey	= document.location.hostname + "_" + gridTitle;
    	return localKey;
    },

    /**
     * 그리드에 새로운 레코드를 추가한다.(이전방식을 그대로 쓰기위하여 만든함수 신규 구성시에는 appendRows를 사용하자.
     * @param id
     * @param rowData
     */
    setInsertRecord: function (id, rowData){
    	if (__grids[id]) {
    		var newRowData = toSplit(rowData, String.fromCharCode(1));
    		this.appendRows(id, [newRowData]);
    	}
    },
    
    setInsertRecords: function (id, rowData){
    	if (__grids[id]) {
    		var newRowData	= new Array();
    		var gridRows	= rowData.split(new RegExp(String.fromCharCode(2),'gi'));
    		for( var i = 0; i < gridRowsro.length; i++ ) {
    			var rowData	= eval("[\""+ gridRows[i].replace(new RegExp(String.fromCharCode(1),'gi'),"\",\"") + "\"]");
	        	newRowData.push(rowData);
    		}
    		this.appendRows(id, newRowData);
    	}
    },
    
    setUpdateRecord : function(id, index, rowData) {
    	var gridView  	= __grids[id].gridView;

	    var newRowData 	= eval("[\""+ rowData.replace(new RegExp(String.fromCharCode(1),'gi'),"\",\"") + "\"]");
	    gridView.setValues(index, newRowData);
    },
    
    /**
     * 그리드 기존 데이터 뒤에 새로운 데이터를 추가한다.
     * @param id
     * @param rowDatas
     */
    appendRows : function (id,rowDatas){
	    var dataProvider = __grids[id].dataProvider;
	    this.insertRows(id, dataProvider.getRowCount(), rowDatas);
    },

    /**
     * 기존 데이터 특정위치에 데이터를 추가한다.
     * @param id
     * @param beginIndex
     * @param rowDatas
     */
    insertRows : function (id, beginIndex, rowDatas){
	    var dataProvider = __grids[id].dataProvider;
	    $.each(rowDatas, function(i, rows) {
	    	$.each(rows, function(idx, row) {
	    		if( typeof rowDatas[i][idx] == "string" ) {
	    			rowDatas[i][idx]	= $.trim(rowDatas[i][idx]);
	    		}
	    		
	    	});
	    });	    
	    dataProvider.insertRows(dataProvider.getRowCount(), rowDatas);
    },

    /**
     * 그리드를 초기화 한다.
     * @param id
     */
    clearGrid: function (id){
	
		if (__grids[id]) {
    		__grids[id].dataProvider.setRowCount(0);
    	}    		
    	
    	
    },
    
    clearRealGrid : function(id) {
    	this.commit(id);
        var gridView  	= __grids[id].gridView;
    	
    	var nRowCount	= this.getGridRowCount(id);
    	if( nRowCount < 1 ) {
    		return;
    	}
    	
    	var columnCount	= this.getColumnCount(id);
    	var value		= null;
    	var isEmptyRow	= true;
    	
    	this.beginUpdate(id);
    	try {
	    	for( var i = (nRowCount-1) ; i >= 0; i-- ) {
	    		isEmptyRow	= true;
	    		
	    		for( var j = 0; j < columnCount; j++ ) {
	    			value	= gridView.getValue(i, j);
	            	if( !is_NullValue(value) ) {
	            		isEmptyRow	= false;
	            		break;
	            	}
	    		}
	    		
	    		if( isEmptyRow ) {
	    			this.setDeleteRecord(id, i);
	    		}
	    	}
    	} catch(e) {
    	} finally {
    		this.endUpdate(id);
    	}
    	
    	return true;
    },
    
    deleteGrid : function(id) {
    	if (__grids[id]) {
    		delete __grids[id];
    		$("#" + id).html("");
    		if(!checkHtml5Browser()) {
    			RealGrids.deleteGrid(id);
    		}
    	}
    },

    /**
     * 그리드 컬럭 고정하기
     * @param id
     */
    fixedGridColumn: function (id, nCount){
    	if (__grids[id]) {
    		__grids[id].gridView.setFixedOptions({colCount : nCount});
    	}
    },

    /**
     * 그리드를 초기화 하고 새로운 데이터를 일괄로 표시한다.
     * @param id
     * @param rowDatas
     */
    setGridRows: function (id, rowDatas){
    	if (__grids[id]) {
    		__grids[id].datas = rowDatas;
    		this.ready(__grids[id]);
    	}
    },

    /**
     * 그리드뷰를 리턴받는다.
     * @param id
     * @returns
     */
    getGridView: function (id){
    	if (__grids[id]) {
    		return __grids[id].gridView;
    	}
    },

    /**
     * 그리드뷰를 그리드에 바인딩된 데이터프로바이더를 리턴받는다.
     * @param id
     * @returns
     */
    getdataProvider: function (id){
    	if (__grids[id]) {
    		return __grids[id].dataProvider;
    	}
    },

    /**
     * 그리드 컬럼을 편집 가능하게 변경
     * @param id
     * @param columnNm
     */
    setEditableColumn : function (id, columnNm, editor){
    	var gridView = __grids[id].gridView;
    	var column   = gridView.columnByName(columnNm);
    	column.editable = true;
    	if (editor) {
    		column.editor = editor;
    		if ("dropDown" == editor.type){
    			gridView.setColumnProperty(columnNm,"lookupDisplay", true);
    			gridView.setColumnProperty(columnNm,"domainOnly", true);
    		}
    	}
    	gridView.setColumn(column);
    },
    
    setColumnWidth : function (id, columnNm, width){
    	var gridView		= __grids[id].gridView;
    	var column   		= gridView.columnByName(columnNm);
    	column.width		= width;
    	column.displayWidth	= width;
    	gridView.setColumn(column);
    },
    
    isComboBoxColumn : function(id, columnNm) {
    	var gridView		= __grids[id].gridView;
    	var column   		= gridView.columnByName(columnNm);
    	if( !is_NullValue(column.editor) && column.editor == "dropDown" ) {
    		return true;
    	}
    	
    	return false;
    },
    
    setComboBoxData : function(id, columnNm, values, labels, dropDownSize) {
    	if( !is_NullValue(values) && !is_NullValue(labels) ) {
    		var valueArray	= values.split(new RegExp(String.fromCharCode(1), 'gi'));
    		var labelArray	= labels.split(new RegExp(String.fromCharCode(1), 'gi'));
    		if( is_NullValue(dropDownSize) )	  dropDownSize = 4;
    		
	    	var gridView 			= __grids[id].gridView;
	    	var column 				= gridView.columnByName(columnNm);
	    	column.lookupDisplay	= true;
	    	
	    	this.beginUpdate(id);
	    	try {
				if( is_NullValue(column.editor) ) {
					column.editor	= {};
				}
				var editorInfo				= column.editor;
				editorInfo.type				= "dropDown";
				editorInfo.dropDownCount	= dropDownSize;
				editorInfo.textReadOnly		= true;
				editorInfo.domainOnly		= true;
				column.editor				= editorInfo;
	
		    	column.labels			= labelArray;
		    	column.values			= valueArray;
		    	column.styles			= {textAlignment : "center"};
		    	gridView.setColumn(column);
	    	} catch(e) {  } finally {
	    		this.endUpdate(id);
	    	}
	    	
	    	// 은행코드 콤보박스 인지 확인
	    	if( this.indexOfStringArray(columnNm.replace(/ /gi, ""), ["은행", "bank"]) != -1 ) {
	    		for( var i = 0; i < valueArray.length; i++ ) {
	    			if( valueArray[i] == "003" && labelArray[i] == "기업은행" ) {
	    				this.addBankCodeColumn(id, columnNm);
	    				this.setBankCodeArray(id, valueArray, labelArray);
	    				break;
	    			}
	    		}
	    	}

    	}
    },
    
    /**
     * __grids[id].events.inputTypes = { "입금계좌번호" : { required : true, maxLength : 10, datType : 0 } };
     */
    setColumnInputTypes : function(id, inputTypeArray) {
    	if( is_NullValue(__grids[id].events) ) {
    		__grids[id].events	= {};	
    	}
       	if( is_NullValue(__grids[id].events.inputTypes) ) {
       		__grids[id].events.inputTypes	= {};
       	}
   		__grids[id].events.inputTypes	= inputTypeArray;
    },
    
    /**
     * __grids[id].events.maxChars = { "입금계좌번호" : 8 };
     *
     * */
    setMaxChars : function(id, columnNm, len) {
    	if( is_NullValue(__grids[id].events) ) {
    		__grids[id].events	= {};	
    	}
    	if( is_NullValue(__grids[id].events.maxChars) ) {
    		__grids[id].events.maxChars	= {};
    	}
    	
    	__grids[id].events.maxChars[columnNm]	= len;
    },
    
    /**
     * __grids[id].events.removeStrings = { "입금계좌번호" : '@'	}; 
     * */
    setColumnRemoveString : function(id, columnNm, str) {
    	if( is_NullValue(__grids[id].events) ) {
    		__grids[id].events	= {};	
    	}
    	if( is_NullValue(__grids[id].events.removeStrings) ) {
    		__grids[id].events.removeStrings	= {};
    	}
    	
    	__grids[id].events.removeStrings[columnNm]	= str;
    },
    
    /**
     * ___grids[id].events.acceptStrings = { "입금계좌번호" : '-' };
     * */
    setColumnAcceptString : function(id, columnNm, str) {
    	if( is_NullValue(__grids[id].events) ) {
    		__grids[id].events	= {};	
    	}
    	if( is_NullValue(__grids[id].events.acceptStrings) ) {
    		__grids[id].events.acceptStrings	= {};
    	}
    	
    	__grids[id].events.acceptStrings[columnNm]	= str;
    },
    
    /**
     * __grids[id].events.conditions = { "입금계좌번호" : [ { type : 1, taget : "sc",     replace : "SC은행" }, 
     * 													    { type : 1, taget : "차타드", replace : "SC은행" } ] };   
     * */
    addConditionFromCaption : function(id, caption, type, target, replace) {
    	if( is_NullValue(__grids[id].events) ) {
    		__grids[id].events	= {};	
    	}
    	if( is_NullValue(__grids[id].events.conditions) ) {
    		__grids[id].events.conditions	= {};
    	}
    	if( is_NullValue(__grids[id].events.conditions[caption]) ) {
    		__grids[id].events.conditions[caption]	= new Array();
    	}
    	
    	__grids[id].events.conditions[caption].push({ type : type,	target : target, replace : replace });
    },
    
    addCellEditChangeCallback : function(id, callback) {
    	if( is_NullValue(__grids[id].events) ) {
    		__grids[id].events	= {};	
    	}
    	if( is_NullValue(__grids[id].events.cellEditChange) ) {
    		__grids[id].events.cellEditChange	= new Array();
    	}
    	
    	__grids[id].events.cellEditChange.push(callback);
    },
       
    addEditRowChangeCallback : function(id, callback) {
    	if( is_NullValue(__grids[id].events) ) {
    		__grids[id].events	= {};	
    	}
    	if( is_NullValue(__grids[id].events.editRowChange) ) {
    		__grids[id].events.editRowChange	= new Array();
    	}
    	
   		__grids[id].events.editRowChange.push(callback);
    },
    
    addCheckAllCallback : function(id, callback) {
    	if( is_NullValue(__grids[id].events) ) {
    		__grids[id].events	= {};	
    	}
    	if( is_NullValue(__grids[id].events.checkAll) ) {
    		__grids[id].events.checkAll	= new Array();
    	}
    	
    	__grids[id].events.checkAll.push(callback);
    },
    
    setUserCodeCondition : function(id, bool) {
    	if( is_NullValue(__grids[id].events) ) {
    		__grids[id].events	= {};	
    	}
    	
    	__grids[id].events.userCodeCondition	= bool;
    },
    
    isUserCodeCondition : function(id) {
    	if( is_NullValue(__grids[id].events) )	return false;
    	
    	return is_True(__grids[id].events.userCodeCondition);
    },
    
    setBankCodeCondition : function(id, bool) {
    	if( is_NullValue(__grids[id].events) ) {
    		__grids[id].events	= {};	
    	}
    	
    	__grids[id].events.bankCodeCondition	= bool;
    },
    
    isBankCodeCondition : function(id) {
    	if( is_NullValue(__grids[id].events) )	return true;
    	if( typeof __grids[id].events.bankCodeCondition == "undefined" || __grids[id].events.bankCodeCondition == null )	return true;
    	
    	return __grids[id].events.bankCodeCondition;
    },
    
    setBankCodeArray : function(id, values, labels) {
    	if( is_NullValue(__grids[id].events) ) {
    		__grids[id].events	= {};	
    	}
    	
    	if( is_NullValue(__grids[id].events.bankCodeArray) || is_NullValue(__grids[id].events.bankNameArray) ) {
    		__grids[id].events.bankCodeArray	= [];
    		__grids[id].events.bankNameArray	= [];
    	}
    	
    	__grids[id].events.bankCodeArray	= values;
    	__grids[id].events.bankNameArray	= labels;
    	
    },
    
    addBankCodeColumn : function(id, columnName) {
    	if( is_NullValue(__grids[id].events) ) {
    		__grids[id].events	= {};	
    	}
    	if( is_NullValue(__grids[id].events.bankCodeColumns) ) {
    		__grids[id].events.bankCodeColumns	= {};
    	}
    	
    	__grids[id].events.bankCodeColumns[columnName]	= columnName;
    },
    
    getBankCodeColumns : function(id) {
    	if( is_NullValue(__grids[id].events) )	return {};
    	
    	return __grids[id].events.bankCodeColumns;
    },
    
    indexOfStringArray : function(str, strArray) {
    	for( var i = 0; i < strArray.length; i++ ) {
    		if( eval("str.search(/"+ strArray[i] + "/gi)") != -1 ) {
    			return 0;
    		}
    	}
    	
    	return -1;
    },
    
    mappingBankCode : function(id, bankCode) {
    	if( is_NullValue(bankCode) )	return "";

    	if( typeof bankCode == "string" && !$.isNumeric(bankCode) ) {
	    	if( !is_NullValue(__grids[id].events.bankNameArray) ) {
	    		for( var i = 0; i < __grids[id].events.bankNameArray.length; i++ ){
	    			if( __grids[id].events.bankNameArray[i] == bankCode ) {
	    				return __grids[id].events.bankCodeArray[i];
	    			}
	    		}
	    	}
    	}
    	
    	if( bankCode.length < 3 && $.isNumeric(bankCode) ) {
    		var forCount	= 3 - bankCode.length;
    		for( var i = 0; i < forCount; i++ ) {
    			bankCode	= "0" + bankCode;
    		}
    	}
    	
    	if( this.indexOfStringArray(bankCode, ["006","009","019","029","030"]) != -1 ) {
    		return "004";
    	} else if( this.indexOfStringArray(bankCode, ["010", "013", "014", "015", "016", "017", "018"]) != -1 ) {
    		return "011";
    	} else if( this.indexOfStringArray(bankCode, ["022", "024", "083", "084"]) != -1 ) {
    		return "020";
    	} else if( this.indexOfStringArray(bankCode, ["036", "053"]) != -1 ) {
    		return "027";
    	} else if( this.indexOfStringArray(bankCode, ["046"]) != -1 ) {
    		return "047";
    	} else if( this.indexOfStringArray(bankCode, ["049"]) != -1 ) {
    		return "048";
    	} else if( this.indexOfStringArray(bankCode, ["072", "073", "074", "075"]) != -1 ) {
    		return "071";
    	} else if( this.indexOfStringArray(bankCode, ["005", "025", "082", "033"]) != -1 ) {
    		return "081";
    	} else if( this.indexOfStringArray(bankCode, ["021", "026", "028", "038", "040"]) != -1 ) {
    		return "088";
    	} else if( this.indexOfStringArray(bankCode, ["산업", "korea development", "korea dev"]) != -1 ) {
    		return "002";
    	} else if( this.indexOfStringArray(bankCode, ["기업", "중소", "중소기업은행", "industrial"]) != -1 ) {
    		return "003";
    	} else if( this.indexOfStringArray(bankCode, ["국민", "주택", "대동", "주택은행", "대동은행", "kookmin", "housing", "commercial"]) != -1 ) {
    		return "004";
    	} else if( this.indexOfStringArray(bankCode, ["외환", "exchange"]) != -1 ) {
    		return "081";
    	} else if( this.indexOfStringArray(bankCode, ["수협", "수협중앙", "수협중앙회", "fisheries"]) != -1 ) {
    		return "007";
    	} else if( this.indexOfStringArray(bankCode, ["수출입", "export", "import"]) != -1 ) {
    		return "008";
    	} else if( this.indexOfStringArray(bankCode, ["단위농협", "단위", "지역농축협"]) != -1 ) {
    		return "012";    		
    	} else if( this.indexOfStringArray(bankCode, ["농협", "축협", "농협회원조합", "농협중앙회", "농업", "농협중앙", "nationalagr", "cooperative fed", "members"]) != -1 ) {
    		return "011";
    	} else if( this.indexOfStringArray(bankCode, ["우리", "평화", "평화은행", "한일", "한일은행", "상업", "상업은행", "한빛", "한빛은행", "(구)한일", "(구)한일은행", "woori"]) != -1 && this.indexOfStringArray(bankCode, ["투", "자", "증권"]) == -1 ) {
    		return "020";
    	} else if( this.indexOfStringArray(bankCode, ["제일", "sc", "sc제일", "sc제일은행", "한국스탠다드차타드", "차타드", "스탠다드", "first"]) != -1 ) {
    		return "023";
    	} else if( this.indexOfStringArray(bankCode, ["씨티", "시티", "한미", "경기", "한국씨티", "한국시티", "한국씨티(구 한미)", "citi", "city"]) != -1 ) {
    		return "027";
    	} else if( this.indexOfStringArray(bankCode, ["대구", "daegu"]) != -1 ) {
    		return "031";
    	} else if( this.indexOfStringArray(bankCode, ["부산", "pusan", "busan"]) != -1 ) {
    		return "032";
    	} else if( this.indexOfStringArray(bankCode, ["광주", "kwangju"]) != -1 ) {
    		return "034";
    	} else if( this.indexOfStringArray(bankCode, ["제주", "cheju"]) != -1 ) {
    		return "035";
    	} else if( this.indexOfStringArray(bankCode, ["전북", "jeonbuk"]) != -1 ) {
    		return "037";
    	} else if( this.indexOfStringArray(bankCode, ["경남", "kyongnam"]) != -1 ) {
    		return "039";
    	} else if( this.indexOfStringArray(bankCode, ["새마", "새마을", "금고", "새마을금고", "새마을금고중앙회"]) != -1 ) {
    		return "045";
    	} else if( this.indexOfStringArray(bankCode, ["신협", "신용", "신용협동", "신용협동조합", "신협중앙회", "credit"]) != -1 ) {
    		return "048";
    	} else if( this.indexOfStringArray(bankCode, ["상호", "저축", "상호저축", "상호저축은행중앙회", "savings", "korea fed"]) != -1 ) {
    		return "050";
    	} else if( this.indexOfStringArray(bankCode, ["홍콩", "홍상", "홍샹", "홍공샹하이(hsbc)", "hongkong", "shanghai", "hsbc"]) != -1 ) {
    		return "054";
    	} else if( this.indexOfStringArray(bankCode, ["도이치", "독일", "deutsche"]) != -1 ) {
    		return "055";
    	} else if( this.indexOfStringArray(bankCode, ["암로", "알비에스", "알비에스은행", "abn", "amro", "abn-amro"]) != -1 ) {
    		return "056";
    	} else if( this.indexOfStringArray(bankCode, ["제이피모간", "제이피모간체이스", "jp모간", "jp모간체이스", "jpmc", "jpmogan"]) != -1 ) {
    		return "057";
    	} else if( this.indexOfStringArray(bankCode, ["미즈호", "미즈호코퍼레이트", "미즈호은행", "미즈호코퍼레이트은행"]) != -1 ) {
    		return "058";
    	} else if( this.indexOfStringArray(bankCode, ["미쓰비시도쿄UFJ", "미쓰비시도쿄", "미쓰비시도쿄은행"]) != -1 ) {
    		return "059";
    	} else if( this.indexOfStringArray(bankCode, ["뱅크오브아메리카", "boa(bank of america)", "boa", "boa은행"]) != -1 ) {
    		return "060";
    	} else if( this.indexOfStringArray(bankCode, ["비엔피파리바", "비엔피파리바은행", "BNP파리바은행"]) != -1 ) {
    		return "061";
    	} else if( this.indexOfStringArray(bankCode, ["하나", "서울", "seoul", "hana"]) != -1 && this.indexOfStringArray(bankCode, ["투", "자", "증권"]) == -1 ) {
    		return "081";
    	} else if( this.indexOfStringArray(bankCode, ["조흥", "신한", "동화", "신한(구 조흥포함)", "shinhan", "chohung"]) != -1 && this.indexOfStringArray(bankCode, ["투", "자", "증권"]) == -1 ) {
    		return "088";
    	} else if( this.indexOfStringArray(bankCode, ["동양", "동양증권"]) != -1 ) {
    		return "209";
    	} else if( this.indexOfStringArray(bankCode, ["현대", "현대증권"]) != -1 ) {
    		return "218";
    	} else if( this.indexOfStringArray(bankCode, ["미래에셋", "미래에셋증권"]) != -1 ) {
    		return "238";
    	} else if( this.indexOfStringArray(bankCode, ["대우", "대우증권", "KDB", "KDB대우증권", "케이디비대우증권"]) != -1 ) {
    		return "238";
    	} else if( this.indexOfStringArray(bankCode, ["삼성", "삼성증권"]) != -1 ) {
    		return "240";
    	} else if( this.indexOfStringArray(bankCode, ["한국투자", "한국투자증권", "한국증권"]) != -1 ) {
    		return "243";
    	} else if( this.indexOfStringArray(bankCode, ["우리투자", "우리투자증권", "우리증권"]) != -1 ) {
    		return "247";
    	} else if( this.indexOfStringArray(bankCode, ["교보", "교보증권"]) != -1 ) {
    		return "261";
    	} else if( this.indexOfStringArray(bankCode, ["하이", "하이투자", "하이투자증권", "하이증권"]) != -1 ) {
    		return "262";
    	} else if( this.indexOfStringArray(bankCode, ["HMC", "HMC투자", "HMC투자증권", "HMC증권", "에이치엠씨", "에이치엠씨투자", "에이치엠씨투자증권", "에이치엠씨증권"]) != -1 ) {
    		return "263";
    	} else if( this.indexOfStringArray(bankCode, ["키움", "키움증권"]) != -1 ) {
    		return "264";
    	} else if( this.indexOfStringArray(bankCode, ["이트레이드", "이트레이드증권"]) != -1 ) {
    		return "265";
    	} else if( this.indexOfStringArray(bankCode, ["SK", "SK증권", "에스케이", "에스케이증권"]) != -1 ) {
    		return "266";
    	} else if( this.indexOfStringArray(bankCode, ["대신", "대신증권"]) != -1 ) {
    		return "267";
    	} else if( this.indexOfStringArray(bankCode, ["아이엠", "아이엠투자", "아이엠투자증권", "아이엠증권"]) != -1 ) {
    		return "268";
    	} else if( this.indexOfStringArray(bankCode, ["한화", "한화투자", "한화투자증권", "한화증권"]) != -1 ) {
    		return "269";
    	} else if( this.indexOfStringArray(bankCode, ["하나대투", "하나대투증권","하나증권"]) != -1 ) {
    		return "270";
    	} else if( this.indexOfStringArray(bankCode, ["신한금융투자", "신한투자", "신한투자증권", "신한증권"]) != -1 ) {
    		return "278";
    	} else if( this.indexOfStringArray(bankCode, ["동부", "동부증권", "DB금투","DB금융투자"]) != -1 ) {
    		return "279";
    	} else if( this.indexOfStringArray(bankCode, ["유진", "유진투자", "유진투자증권", "유진증권"]) != -1 ) {
    		return "280";
    	} else if( this.indexOfStringArray(bankCode, ["메리츠", "메리츠종합금융증권", "메리츠종합증권", "메리츠증권"]) != -1 ) {
    		return "287";
    	} else if( this.indexOfStringArray(bankCode, ["HN농협증권", "NH증권", "농협증권"]) != -1 ) {
    		return "289";
    	} else if( this.indexOfStringArray(bankCode, ["부국", "부국증권"]) != -1 ) {
    		return "290";
    	} else if( this.indexOfStringArray(bankCode, ["신영", "신영증권"]) != -1 ) {
    		return "291";
    	} else if( this.indexOfStringArray(bankCode, ["LIG", "LIG투자", "LIG투자증권", "LIG증권", "엘아이지증권", "엘아이지투자증권"]) != -1 ) {
    		return "292";
    	} else if( this.indexOfStringArray(bankCode, ["케이뱅크", "K뱅크"]) != -1 ) {
    		return "089";
    	} else if( this.indexOfStringArray(bankCode, ["카뱅", "kakao", "카카오뱅크"]) != -1 ) {
    		return "090";    		
    	} else if( this.indexOfStringArray(bankCode, ["은행"]) != -1 ) {
    		if( this.indexOfStringArray(bankCode, ["산업"]) != -1 ) {
    			return "002";
    		} else if( this.indexOfStringArray(bankCode, ["기업", "중소"]) != -1 ) {
    			return "003";
    		} else if( this.indexOfStringArray(bankCode, ["수협", "중앙"]) != -1 ) {
    			return "007";
    		} else if( this.indexOfStringArray(bankCode, ["수출입"]) != -1 ) {
    			return "008";
    		} else if( this.indexOfStringArray(bankCode, ["농협", "축협", "단위", "농업"]) != -1 ) {
    			return "011";
    		} else if( this.indexOfStringArray(bankCode, ["우리", "평화", "한일", "상업", "한빛"]) != -1 ) {
    			return "020";
    		} else if( this.indexOfStringArray(bankCode, ["제일", "sc"]) != -1 ) {
    			return "023";
    		} else if( this.indexOfStringArray(bankCode, ["씨티", "시티", "한미", "경기", "한국"]) != -1 ) {
    			return "027";
    		} else if( this.indexOfStringArray(bankCode, ["대구"]) != -1 ) {
    			return "031";
    		} else if( this.indexOfStringArray(bankCode, ["부산"]) != -1 ) {
    			return "032";
    		} else if( this.indexOfStringArray(bankCode, ["광주"]) != -1 ) {
    			return "034";
    		} else if( this.indexOfStringArray(bankCode, ["제주"]) != -1 ) {
    			return "035";
    		} else if( this.indexOfStringArray(bankCode, ["전북"]) != -1 ) {
    			return "037";
    		} else if( this.indexOfStringArray(bankCode, ["경남"]) != -1 ) {
    			return "039";
    		} else if( this.indexOfStringArray(bankCode, ["새마을", "금고"]) != -1 ) {
    			return "045";
    		} else if( this.indexOfStringArray(bankCode, ["신협", "신용"]) != -1 ) {
    			return "048";
    		} else if( this.indexOfStringArray(bankCode, ["상호", "저축"]) != -1 ) {
    			return "050";
    		} else if( this.indexOfStringArray(bankCode, ["홍콩", "홍상", "홍샹", "샹하이", "hsbc"]) != -1 ) {
    			return "054";
    		} else if( this.indexOfStringArray(bankCode, ["도이치", "독일"]) != -1 ) {
    			return "055";
    		} else if( this.indexOfStringArray(bankCode, ["암로"]) != -1 ) {
    			return "056";
    		} else if( this.indexOfStringArray(bankCode, ["제이피", "모간", "체이스", "jp"]) != -1 ) {
    			return "057";
    		} else if( this.indexOfStringArray(bankCode, ["아메리카"]) != -1 ) {
    			return "060";
    		} else if( this.indexOfStringArray(bankCode, ["우체국", "우체"]) != -1 ) {
    			return "071";
    		} else if( this.indexOfStringArray(bankCode, ["하나", "서울", "외환"]) != -1 ) {
    			return "081";
    		} else if( this.indexOfStringArray(bankCode, ["조흥", "신한", "동화"]) != -1 ) {
    			return "088";
    		}
    	}
    	
    	return bankCode;
    },
    
    executeConditions : function(id) {
    	var dataProvider	= __grids[id].dataProvider;
    	var gridView		= __grids[id].gridView;
    	
    	// 은행코드 매핑
        var bankCodeColumns		= this.getBankCodeColumns(id);
		if( !is_NullValue(bankCodeColumns) ) {
				
			this.beginUpdate(id);
			try {
                var dataRowCount		= this.getGridRowCount(id);
                for( var i = 0; i < dataRowCount; i++ ) {

	    			$.each(bankCodeColumns, function(k, v) {
            			var data		= gridView.getValue(i, k);
            			if( !is_NullValue(data) ) {
        	        		if( typeof data == "string" ) {
        	        			data	= $.trim(data);
        	        		}
            				data	= RgUtil.mappingBankCode(id, data);
	            			gridView.setValue(i, k, data);
            			}
            		});
                }
			} catch(e) {  } finally {
				this.endUpdate(id);
			}
		}
    	
    	// 콤보박스
    	var displayColumns	= this.getDisplayColumns(id);
    	$.each(displayColumns, function(idx, col) {
    		var column   	= RgUtil.getColumnByName(id, col);
    		if( !is_NullValue(column.editor) ) {
    			
    			if( column.editor == "dropDown" ) {
    	    		var mapping		= true;
    	    		if( !is_NullValue(bankCodeColumns) ) {
    	        		for( var i = 0; i < bankCodeColumns.length; i++ ) {
    	        			if( bankCodeColumns[i] == col ) {
    	        				mapping	= false;
    	        				break;
    	        			}
    	        		}
    	    		}
    	    		
    	    		if( mapping ) {
    	    			var valuesArray	= column.values;
    	    			var labelsArray	= column.labels;
    	    			
    	    			RgUtil.beginUpdate(id);
    	    			try {
    	                    var dataRowCount	= RgUtil.getGridRowCount(id);
    	                    for( var i = 0; i < dataRowCount; i++ ) {
	                			var data		= gridView.getValue(i, col);
	                			if( !is_NullValue(data) ) {
	            	        		if( typeof data == "string" ) {
	            	        			data	= $.trim(data);
	            	        		}
	                				
	            	        		for( var j = 0; j < labelsArray.length; j++ ) {
	            	        			if( labelsArray[j] == data ) {
	            	        				data	= valuesArray[j];
	            	        				break;
	            	        			}
	            	        		}
	            	        		
	    	            			gridView.setValue(i, col, data);
	                			}
    	                    }
    	    			} catch(e) {  } finally {
    	    				RgUtil.endUpdate(id);
    	    			}
    	    		}
    			}
    		}
    	});
		
    	if( !is_NullValue(__grids[id].events) ) {
	    	
	    	// 컬럼 condition 설정
	    	if( this.isUserCodeCondition(id) && !is_NullValue(__grids[id].events.conditions) ) {
				this.beginUpdate(id);
				try {
	                var dataRowCount	= this.getGridRowCount(id);
	                for( var i = 0; i < dataRowCount; i++ ) {
	                	
	            		$.each(__grids[id].events.conditions, function(key, arr) {
	            			var data	= gridView.getValue(i, key);
	            			if( !is_NullValue(data) ) {
		            			$.each(arr, function(i, v){
		            				if( v.type == 0 ) {				// 0 : 모두 일치하는 경우
	    	            				if( data == v.target ) {
	    	            					data	= v.replace;
	    	            				}
	    	            			} else if( v.type == 1 ) {		// 1 : 포함된 경우
	    	            				if( data.indexOf(v.target) != -1 ) {
	    	            					data	= v.replace;
	    	            				}
	    	            			}
		            			});
		            			
		            			gridView.setValue(i, key, data);
	            			}
	            		});
	                }
				} catch(e) {  } finally {
					this.endUpdate(id);
				}
	
	    	}
    	}
    },
	
	bindEvents : function(id) {
		// 이벤트 처리
    	if( !is_NullValue(__grids[id].events) ) {
        	var dataProvider	= __grids[id].dataProvider;
        	var gridView		= __grids[id].gridView;

        	// Cell Edit 관련 이벤트 처리
        	if( !is_NullValue(__grids[id].events.inputTypes) || !is_NullValue(__grids[id].events.maxChars) || !is_NullValue(__grids[id].events.removeStrings) || !is_NullValue(__grids[id].events.acceptStrings) ) {
        		
	    		gridView.onCellEdited = function(gridObj, itemIndex, dataRow, field) {
	    			var fieldName	= dataProvider.getFieldName(field);
					var cellValue	= gridObj.getValue(itemIndex, field);

					if( !is_NullValue(cellValue) ) {

						this.beginUpdate(id);
						try {
							// Cell 입력 inputType 설정
							if( !is_NullValue(__grids[id].events.inputTypes) && !is_NullValue(__grids[id].events.inputTypes[fieldName]) ) {
	
								var acceptStr	= "";
				            	// Cell 입력 허용 String 설정
				            	if( !is_NullValue(__grids[id].events.acceptStrings) && !is_NullValue(__grids[id].events.acceptStrings[fieldName]) ) {
				            		var str		= __grids[id].events.acceptStrings[fieldName];
				            		for( var i = 0; i < str.length; i++ ) {
				            			if( i == 0 ) {
				            				acceptStr	+= str.slice(i, i+1);
				            			} else {
				            				acceptStr	+= "," + str.slice(i, i+1);
				            			}
				            		}
				            	}
								
				            	// datType
				            	if( !is_NullValue(__grids[id].events.inputTypes[fieldName].datType) ) {
									// { "입금계좌번호" : { required : true, maxLength : 10, datType : 0 } }
									var datType	= __grids[id].events.inputTypes[fieldName].datType;
									switch( datType ) {
										 // 영문+한글+숫자(ctAHN = 0)
										 case 0 : break;
										 // 영문만(ctA   = 1)
										 case 1 : 
											 	  if( eval("cellValue.search(/[^a-zA-Z" + acceptStr + "]/gi)") != -1 ) {
											 		  var _infoMsg	= "[" + fieldName + "] 컬럼은 영문만 입력가능합니다.";
												 	  if( acceptStr.length > 0 ) {
												 		 _infoMsg	= "[" + fieldName + "] 컬럼은 영문과 "+acceptStr+" 문자만 입력가능합니다.";
												 	  }
												 	  alert(_infoMsg);
												 	  gridObj.setValue(itemIndex, field, "");
								            		  gridObj.setCurrent({ itemIndex : itemIndex, column : fieldName });
											 	  }
											 	  break;
										 // 한글만(ctH   = 2)
										 case 2 : 
											 	  if( eval("cellValue.search(/[^\u3131-\u318E\uAC00-\uD7A3" + acceptStr + "]/gi)") != -1 ) {
											 		  var _infoMsg	= "[" + fieldName + "] 컬럼은 한글만 입력가능합니다.";
												 	  if( acceptStr.length > 0 ) {
												 		 _infoMsg	= "[" + fieldName + "] 컬럼은 한글과 "+acceptStr+" 문자만 입력가능합니다.";
												 	  }
												 	  alert(_infoMsg);
												 	  gridObj.setValue(itemIndex, field, "");
								            		  gridObj.setCurrent({ itemIndex : itemIndex, column : fieldName });
											 	  }
											 	  break;
										 // 숫자만(ctN   = 3)
										 case 3 : 
											 	  if( eval("cellValue.search(/[^0-9" + acceptStr + "]/gi)") != -1 ) {
											 		  var _infoMsg	= "[" + fieldName + "] 컬럼은 숫자만 입력가능합니다.";
												 	  if( acceptStr.length > 0 ) {
												 		 _infoMsg	= "[" + fieldName + "] 컬럼은 숫자와 "+acceptStr+" 문자만 입력가능합니다.";
												 	  }
												 	  alert(_infoMsg);
												 	  gridObj.setValue(itemIndex, field, "");
								            		  gridObj.setCurrent({ itemIndex : itemIndex, column : fieldName });
											 	  }
											      break;
										 // 영문+숫자(ctAN  = 4)
										 case 4 :
											 	  if( eval("cellValue.search(/[^a-zA-Z0-9" + acceptStr + "]/gi)") != -1 ) {
											 		  var _infoMsg	= "[" + fieldName + "] 컬럼은 영문, 숫자만 입력가능합니다.";
												 	  if( acceptStr.length > 0 ) {
												 		 _infoMsg	= "[" + fieldName + "] 컬럼은 영문, 숫자와 "+acceptStr+" 문자만 입력가능합니다.";
												 	  }
												 	  alert(_infoMsg);
												 	  gridObj.setValue(itemIndex, field, "");
								            		  gridObj.setCurrent({ itemIndex : itemIndex, column : fieldName });
											 	  }
											      break;
										 // 한글+숫자(ctHN  = 5)
										 case 5 : 
											 	  if( eval("cellValue.search(/[^0-9\u3131-\u318E\uAC00-\uD7A3" + acceptStr + "]/gi)") != -1 ) {
											 		  var _infoMsg	= "[" + fieldName + "] 컬럼은 한글, 숫자만 입력가능합니다.";
												 	  if( acceptStr.length > 0 ) {
												 		 _infoMsg	= "[" + fieldName + "] 컬럼은 한글, 숫자와 "+acceptStr+" 문자만 입력가능합니다.";
												 	  }
												 	  alert(_infoMsg);
												 	  gridObj.setValue(itemIndex, field, "");
								            		  gridObj.setCurrent({ itemIndex : itemIndex, column : fieldName });
											 	  }
											      break;
										 // 영문+한글(ctHN  = 5)
										 case 6 : 
											 	  if( eval("cellValue.search(/[^a-zA-Z\u3131-\u318E\uAC00-\uD7A3" + acceptStr + "]/gi)") != -1 ) {
											 		  var _infoMsg	= "[" + fieldName + "] 컬럼은 영문, 한글만 입력가능합니다.";
												 	  if( acceptStr.length > 0 ) {
												 		 _infoMsg	= "[" + fieldName + "] 컬럼은 영문, 한글과 "+acceptStr+" 문자만 입력가능합니다.";
												 	  }
												 	  alert(_infoMsg);
												 	  gridObj.setValue(itemIndex, field, "");
								            		  gridObj.setCurrent({ itemIndex : itemIndex, column : fieldName });
											 	  }
											      break;
										 // (ctLAN = 7)
										 case 7 : break;
									}
									
				            	}
				            	
							}
							
			            	// Cell 입력 삭제 String 설정
			            	if( !is_NullValue(__grids[id].events.removeStrings) && !is_NullValue(__grids[id].events.removeStrings[fieldName]) ) {
			            		var removeStr	= "";
			            		var str			= __grids[id].events.removeStrings[fieldName];
			            		for( var i = 0; i < str.length; i++ ) {
			            			if( i == 0 ) {
			            				removeStr	+= str.slice(i, i+1);
			            			} else {
			            				removeStr	+= "," + str.slice(i, i+1);
			            			}
			            		}
			            		
			            		if( eval("cellValue.search(/[" + removeStr + "]/gi)") != -1 ) {
			            			alert("[" + fieldName + "] 컬럼에 허용되지 않는 문자가 입력되었습니다.\r\n"+removeStr);
			            			gridObj.setValue(itemIndex, field, "");
			            			gridObj.setCurrent({ itemIndex : itemIndex, column : fieldName });
		        				}
			            	}
							
			            	// Cell maxChars 설정
			            	if( !is_NullValue(__grids[id].events.maxChars) && !is_NullValue(__grids[id].events.maxChars[fieldName]) ) {
			            		var maxChars	= __grids[id].events.maxChars[fieldName];
			            		if( cellValue.length > maxChars ) {
		        					alert("[" + fieldName + "] 컬럼의 최대 입력길이는 "+ maxChars + "자리입니다.");
		        					cellValue	= cellValue.substring(0, maxChars);
		        					gridObj.setValue(itemIndex, field, cellValue);
		        					gridObj.setCurrent({ itemIndex : itemIndex, column : fieldName });
		        				}
			            	}
						} catch(e) {  } finally {
							this.endUpdate(id);
						}
					}
					
					// Cell 데이터 변경시 콜백함수 호출
					if( !is_NullValue(__grids[id].events.cellEditChange) ) {
	            		var callbackArray	= __grids[id].events.cellEditChange;
	            		$.each(callbackArray, function(i, callback) {
	                		if( typeof callback == "string" ) {
	                			eval(callback+"(gridObj, itemIndex, dataRow, field)");
	                		} else if( typeof callback == "function" || typeof callback == "object" ) {
	                			callback(gridObj, itemIndex, dataRow, field);
	                		}
	            		});
					}

	    		};
        	}
        	
        	// 편집그리드 Row 변경 이벤트 처리
        	if( !is_NullValue(__grids[id].events.editRowChange) ) {
        		dataProvider.onRowCountChanged = function(providerObj, count) {
        			if( count > 0 ) {
	            		var callbackArray	= __grids[id].events.editRowChange;
	            		$.each(callbackArray, function(i, callback) {
	                		if( typeof callback == "string" ) {
	                			eval(callback+"(providerObj , 0, 0)");
	                		} else if( typeof callback == "function" || typeof callback == "object" ) {
	                			callback(providerObj, 0, 0);
	                		}
	            		});
        			}
        		};
        		
        		gridView.onCurrentRowChanged = function(gridObj, oldRow, newRow) {
            		var callbackArray	= __grids[id].events.editRowChange;
            		$.each(callbackArray, function(i, callback) {
                		if( typeof callback == "string" ) {
                			eval(callback+"(gridObj , 0, 0)");
                		} else if( typeof callback == "function" || typeof callback == "object" ) {
                			callback(gridObj, 0, 0);
                		}
            		});
        		};

        	}

        	// 편집그리드 checkAll 이벤트 처리
        	if( !is_NullValue(__grids[id].events.checkAll) ) {
            	gridView.onItemAllChecked	= function(gridObj, checked) {
            		var callbackArray	= __grids[id].events.checkAll;
            		$.each(callbackArray, function(i, callback) {
                		if( typeof callback == "string" ) {
                			eval(callback+"(gridObj ,"+checked+")");
                		} else if( typeof callback == "function" || typeof callback == "object" ) {
                			callback(gridObj, checked);
                		}
            		});
            	};    		
        	}
        	
    	}    	
	},
	
	commit : function(id) {
		if( !is_NullValue(__grids[id]) ) {
		    var gridView = __grids[id].gridView;
		    try {
		    	gridView.commit(true);
		    } catch(e) {
		    	var errMessage	= e.message;
		    	if( errMessage.indexOf("컬럼은 필수입력 항목입니다.") != -1 ) {
		    		alert(errMessage);
		    		throw "";
		    	}
		    }
		}
	},
   
    getColumnByName : function(id, columnNm) {
	    var gridView = __grids[id].gridView;
	    return gridView.columnByName(columnNm);
    },
    
    getRowData: function (id, index){
        var gridView  = null;
        var rowData   = [];

        gridView    = __grids[id].gridView;
        rowData[0]  = gridView.getDataSource().getJsonRow(index);
        return rowData;
    },
    
    jsonArray2Array : function(jsonArrayData) {
    	var gridDataArray		= new Array();
    	$.each(jsonArrayData, function(i, rows) {
    		var rowDataArray	= new Array();
    		$.each(rows, function(j, data) {
    			rowDataArray.push(data);
    		});
    		gridDataArray.push(rowDataArray);
    	});
    	
    	return gridDataArray;
    },
    
    getDisplayValues2Array : function(id, startIndex, endIndex, columnCount, parentFields, parentId) {
    	if( is_NullValue(startIndex) )	startIndex	= 0;
    	var gridRowCount						= this.getGridRowCount(id);
    	if( is_NullValue(endIndex) )     endIndex 	= gridRowCount - 1;
    	if( endIndex > gridRowCount ) endIndex	= gridRowCount - 1;
    	var checkColumnCount					= is_NullValue(columnCount) ? false : true;
    	
        var dataProvider	= __grids[id].dataProvider;
        var gridView   		= __grids[id].gridView;
        var displayColumns	= this.getDisplayColumns(id);
        
        // 은행코드인 경우 파일에 작성된 은행코드 매핑 처리
        var bankCodeCondition	= true;
        var bankCodeColumns		= null;
        if( !is_NullValue(parentId) ) {
	        bankCodeCondition	= this.isBankCodeCondition(parentId);
	        bankCodeColumns		= this.getBankCodeColumns(parentId);
	        if( is_NullValue(bankCodeColumns) ) {
	        	bankCodeColumns	= {};
	        }
        }
        
        if( is_NullValue(parentFields) ) {
        	var fields			= new Array();
        	$.each(displayColumns, function(index, value) {
        		var fieldIndex	= dataProvider.getFieldIndex(value);
        		var field		= dataProvider.getFields()[fieldIndex];
        		fields.push(field);
        	});
        	
        	parentFields	= fields;
        }
		
        var rowsArrayData	= new Array();
        for( var i = startIndex; i <= endIndex; i++ ) {
        	var rowData		= new Array();
        	for( var j = 0; j < displayColumns.length; j++ ) {
        		// 데이터 최대 건수
        		if( checkColumnCount ) {
            		if( j >= columnCount ) {
            			break;
            		}
        		}
        		
        		// 필드의 dataType을 확인하여 데이터 변환 처리
        		var fieldData	= this.getDisplayValue(id, i, displayColumns[j]);
        		if( is_NullValue(fieldData) ) {
        			fieldData	= "";
        		} else {
	        		if( typeof fieldData == "string" ) {
	        			fieldData	= $.trim(fieldData);
	        		}
        			
	        		if( !is_NullValue(parentFields) ) {
	        			if ( parentFields[j].dataType == "datetime" ) {
	        				if( typeof fieldData == "string" ) {
	        					fieldData	= fieldData.replace(/[^0-9]/gi, "");
	        					fieldData	= fieldData.substring(0, 4) + "-" + fieldData.substring(4, 6) + "-" + fieldData.substring(6);
	        				} else {
	        					var newDate	= new Date(fieldData);
	        					fieldData	= newDate.getFullYear() + "-" + ("0"+(newDate.getMonth()+1)).slice(-2) + "-" + ("0"+(newDate.getDate())).slice(-2);
	        				}
	        			} else if( parentFields[j].dataType == "text" ) {
	        				// 은행코드인 경우 파일에 작성된 은행코드 매핑 처리
	        				if( !bankCodeCondition ) {
	        					if( !is_NullValue(bankCodeColumns[parentFields[j].fieldName]) ) {
	        						fieldData	= this.mappingBankCode(id, fieldData);
	        					}
	        				}

	        			}
	        		}
        		}
        		
        		rowData.push(fieldData);        		
        	}
        	
        	rowsArrayData.push(rowData);
        }
        
        return rowsArrayData;
    },
    
    getDisplayRows2Array : function(id, startIndex, endIndex, columnCount, parentFields, parentId) {
    	if( is_NullValue(startIndex) )	startIndex	= 0;
    	var gridRowCount						= this.getItemCount(id);
    	if( is_NullValue(endIndex) )     endIndex 	= gridRowCount - 1;
    	if( endIndex > gridRowCount ) endIndex	= gridRowCount - 1;
    	var checkColumnCount					= is_NullValue(columnCount) ? false : true;
    	
        var dataProvider	= __grids[id].dataProvider;
        var gridView   		= __grids[id].gridView;
        var displayColumns	= this.getDisplayColumns(id);
        var newColumnNames	= new Array();
        $.each(displayColumns, function(i, col) {
        	if( typeof col == "string" ) {
        		newColumnNames.push(col);
        		// 그룹
        	} else {
        		$.each(col.columns, function(gidx, columnName) {
        			newColumnNames.push(columnName);
        		});
        	}
        });
        if( newColumnNames.length > 0 ) {
        	displayColumns	= newColumnNames;
        }
        
        var parentColumns	= new Array();
        
        // 은행코드인 경우 파일에 작성된 은행코드 매핑 처리
        var bankCodeCondition	= true;
        var bankCodeColumns		= null;
        if( !is_NullValue(parentId) ) {
	        bankCodeCondition	= this.isBankCodeCondition(parentId);
	        bankCodeColumns		= this.getBankCodeColumns(parentId);
	        if( is_NullValue(bankCodeColumns) ) {
	        	bankCodeColumns	= {};
	        }
	        
	        $.each(parentFields, function(i, f) {
        		var column   	= this.getColumnByName(parentId, f.fieldName);
        		parentColumns.push(column);
        	});
        }
        
        if( is_NullValue(parentFields) ) {
        	var fields			= new Array();
        	$.each(displayColumns, function(index, value) {
        		var fieldIndex	= dataProvider.getFieldIndex(value);
        		var field		= dataProvider.getFields()[fieldIndex];
        		fields.push(field);
        		
        		var column   	= gridView.columnByName(value);
        		parentColumns.push(column);
        	});
        	
        	parentFields	= fields;
        }
		
        var rowsArrayData	= new Array();
        for( var i = startIndex; i <= endIndex; i++ ) {
        	if( gridView.getDataRow(i) == -1 ) {
        		continue;
        	}
        	
        	var rowData		= new Array();
        	for( var j = 0; j < displayColumns.length; j++ ) {
        		// 데이터 최대 건수
        		if( checkColumnCount ) {
            		if( j >= columnCount ) {
            			break;
            		}
        		} 
        		
        		// 필드의 dataType을 확인하여 데이터 변환 처리
        		var fieldData	= gridView.getValue(i, displayColumns[j]);
        		if( is_NullValue(fieldData) ) {
        			fieldData	= "";
        		} else {
	        		if( typeof fieldData == "string" ) {
	        			fieldData	= $.trim(fieldData);
	        		}
        			
	        		if( !is_NullValue(parentFields) ) {
	        			if( !is_NullValue(parentColumns[j].editor) && parentColumns[j].editor == "dropDown" ) {
	        				fieldData	= this.getText(id, i, displayColumns[j]);
	        			} else if ( parentFields[j].dataType == "datetime" ) {
	        				if( typeof fieldData == "string" ) {
	        					fieldData	= fieldData.replace(/[^0-9]/gi, "");	        					
	        				} else {
	        					var newDate	= new Date(fieldData);
	        					fieldData	= newDate.getFullYear() + ("0"+(newDate.getMonth()+1)).slice(-2) + ("0"+(newDate.getDate())).slice(-2);
	        				}
	        			} else if( parentFields[j].dataType == "text" ) {
	        				// 은행코드인 경우 파일에 작성된 은행코드 매핑 처리
	        				if( !bankCodeCondition ) {
	        					if( !is_NullValue(bankCodeColumns[parentFields[j].fieldName]) ) {
	        						fieldData	= this.mappingBankCode(id, fieldData);
	        					}
	        				}

	        			}
	        		}
        		}
        		
        		rowData.push(fieldData);        		
        	}
        	
        	rowsArrayData.push(rowData);
        }
        
        return rowsArrayData;
    },
    
    getDisplayRowsArrayData : function(id, startIndex, endIndex, columnCount, parentFields, parentId) {
    	if( is_NullValue(startIndex) )	startIndex	= 0;
    	var gridRowCount						= this.getGridRowCount(id);
    	if( is_NullValue(endIndex) )     endIndex 	= gridRowCount - 1;
    	if( endIndex > gridRowCount ) endIndex	= gridRowCount - 1;
    	var checkColumnCount					= is_NullValue(columnCount) ? false : true;
    	
        var dataProvider	= __grids[id].dataProvider;
        var gridView   		= __grids[id].gridView;
        var displayColumns	= this.getDisplayColumns(id);
        
        // 은행코드인 경우 파일에 작성된 은행코드 매핑 처리
        var bankCodeCondition	= true;
        var bankCodeColumns		= null;
        if( !is_NullValue(parentId) ) {
	        bankCodeCondition	= this.isBankCodeCondition(parentId);
	        bankCodeColumns		= this.getBankCodeColumns(parentId);
	        if( is_NullValue(bankCodeColumns) ) {
	        	bankCodeColumns	= {};
	        }
        }
        
        if( is_NullValue(parentFields) ) {
        	var fields			= new Array();
        	$.each(displayColumns, function(index, value) {
        		var fieldIndex	= dataProvider.getFieldIndex(value);
        		var field		= dataProvider.getFields()[fieldIndex];
        		fields.push(field);
        	});
        	
        	parentFields	= fields;
        }
		
        var rowsArrayData	= new Array();
        for( var i = startIndex; i <= endIndex; i++ ) {
        	var rowData		= new Array();
        	for( var j = 0; j < displayColumns.length; j++ ) {
        		// 데이터 최대 건수
        		if( checkColumnCount ) {
            		if( j >= columnCount ) {
            			break;
            		}
        		}
        		
        		// 필드의 dataType을 확인하여 데이터 변환 처리
        		var fieldData	= gridView.getValue(i, displayColumns[j]);
        		if( is_NullValue(fieldData) ) {
        			fieldData	= "";
        		} else {
	        		if( typeof fieldData == "string" ) {
	        			fieldData	= $.trim(fieldData);
	        		}
        			
	        		if( !is_NullValue(parentFields) ) {
	        			if ( parentFields[j].dataType == "datetime" ) {
	        				if( typeof fieldData == "string" ) {
	        					fieldData	= fieldData.replace(/[^0-9]/gi, "");	        					
	        				} else {
	        					var newDate	= new Date(fieldData);
	        					fieldData	= newDate.getFullYear() + ("0"+(newDate.getMonth()+1)).slice(-2) + ("0"+(newDate.getDate())).slice(-2);
	        				}
	        			} else if( parentFields[j].dataType == "number" ) {
	        				if( typeof fieldData == "string" ) {
	        					fieldData	= fieldData.replace(/,/gi, "");
	        				}
	        			} else if( parentFields[j].dataType == "text" ) {
	        				// 은행코드인 경우 파일에 작성된 은행코드 매핑 처리
	        				if( !bankCodeCondition ) {
	        					if( !is_NullValue(bankCodeColumns[parentFields[j].fieldName]) ) {
	        						fieldData	= this.mappingBankCode(id, fieldData);
	        					}
	        				}

	        			}
	        		}
        		}
        		
        		rowData.push(fieldData);        		
        	}
        	
        	rowsArrayData.push(rowData);
        }
        
        return rowsArrayData;
    },
    
    getDisplayDataRows2Json : function(id, startIndex, endIndex, columnCount, parentFields, parentId) {
    	if( is_NullValue(startIndex) )	startIndex	= 0;
    	var gridRowCount						= this.getGridRowCount(id);
    	if( is_NullValue(endIndex) )     endIndex 	= gridRowCount - 1;
    	if( endIndex > gridRowCount ) endIndex	= gridRowCount - 1;
    	var checkColumnCount					= is_NullValue(columnCount) ? false : true;
    	
        var dataProvider	= __grids[id].dataProvider;
        var gridView   		= __grids[id].gridView;
        var displayColumns	= this.getDisplayColumns(id);
        var parentDisplayColumns	= null; 
        
        // 은행코드인 경우 파일에 작성된 은행코드 매핑 처리
        var bankCodeCondition	= true;
        var bankCodeColumns		= null;
        if( !is_NullValue(parentId) ) {
        	parentDisplayColumns	= this.getDisplayColumns(parentId);
        	
	        bankCodeCondition	= this.isBankCodeCondition(parentId);
	        bankCodeColumns		= this.getBankCodeColumns(parentId);
	        if( is_NullValue(bankCodeColumns) ) {
	        	bankCodeColumns	= {};
	        }
        }
        
        if( is_NullValue(parentFields) ) {
        	var fields			= new Array();
        	$.each(displayColumns, function(index, value) {
        		var fieldIndex	= dataProvider.getFieldIndex(value);
        		var field		= dataProvider.getFields()[fieldIndex];
        		fields.push(field);
        	});
        	
        	parentFields	= fields;
        }
		
        var rowsArrayData	= new Array();
        for( var i = startIndex; i <= endIndex; i++ ) {
        	var rowData		= {};
        	for( var j = 0; j < displayColumns.length; j++ ) {
        		// 데이터 최대 건수
        		if( checkColumnCount ) {
            		if( j >= columnCount ) {
            			break;
            		}
        		}
        		
        		// 필드의 dataType을 확인하여 데이터 변환 처리
        		var fieldData	= gridView.getValue(i, displayColumns[j]);
        		if( is_NullValue(fieldData) ) {
        			fieldData	= "";
        		} else {
	        		if( typeof fieldData == "string" ) {
	        			fieldData	= $.trim(fieldData);
	        		}
        			
	        		if( !is_NullValue(parentFields) ) {
	        			if ( parentFields[j].dataType == "datetime" ) {
	        				if( typeof fieldData == "string" ) {
	        					fieldData	= fieldData.replace(/[^0-9]/gi, "");	        					
	        				} else {
	        					var newDate	= new Date(fieldData);
	        					fieldData	= newDate.getFullYear() + ("0"+(newDate.getMonth()+1)).slice(-2) + ("0"+(newDate.getDate())).slice(-2);
	        				}
	        			} else if( parentFields[j].dataType == "number" ) {
	        				if( typeof fieldData == "string" ) {
	        					fieldData	= fieldData.replace(/,/gi, "");
	        				}
	        			} else if( parentFields[j].dataType == "text" ) {
	        				// 은행코드인 경우 파일에 작성된 은행코드 매핑 처리
	        				if( !bankCodeCondition ) {
	        					if( !is_NullValue(bankCodeColumns[parentFields[j].fieldName]) ) {
	        						fieldData	= this.mappingBankCode(id, fieldData);
	        					}
	        				}

	        			}
	        		}	        		
        		}

        		rowData[parentDisplayColumns[j]]	= fieldData;
        	}

        	rowsArrayData.push(rowData);
        }
        
        return rowsArrayData;
    },
    
    getDisplayGridRowCount : function(id) {
        var gridView  			= __grids[id].gridView;
    	
    	var displayGridRowCount	= 0;
    	var dataRowCount		= this.getGridRowCount(id);
        var displayColumns		= this.getDisplayColumns(id);
        
        var value		= null;
    	var addCount	= false;
        for( var i = 0; i < dataRowCount; i++ ) {
    		addCount	= false;
        	
        	for( j = 0; j < displayColumns.length; j++ ) {
	        	value	= gridView.getValue(i, displayColumns[0]);
	        	if( !is_NullValue(value) ) {
	        		addCount	= true;
	        		break;
	        	}
        	}
        	
    		if( addCount ) {
    			displayGridRowCount++;
    		}
        }
        
        return displayGridRowCount;
    },
    
    getFocusRowData: function (id){
        var gridView  = __grids[id].gridView;
        var focusCell = gridView.getCurrent();
        return this.getRowData(id, focusCell.dataRow);
    },
    getCheckedRowDatas: function (id){
    	//IE8 수정. 20170210 조대호
		var gridView  = __grids[id].gridView;

		/*var rowData   = [];
		var rowIdxs   = gridView.getCheckedRows();

		for (var i = 0 ; i < rowIdxs.length ; i ++){
			rowData[i] = gridView.getDataSource().getJsonRow(rowIdxs[i]);
		}*/

		
		var rowDatas    = [];
		var rowData     = {};
		//var rowIdxs     = gridView.getCheckedRows();
		var rowIdxs     = gridView.getCheckedItems();
		var columnNames = gridView.getColumnNames(false);
		
		for(var i=0; i<rowIdxs.length; i++){
			for(var j=0; j<columnNames.length; j++){
				rowData[columnNames[j]] = RgUtil.getCell(id, rowIdxs[i], columnNames[j]);
			}

			rowDatas[i] = rowData;
		}

		return rowDatas;
    },
     setShowSelectedRow : function (id, index){
    	try {
    		var gridView = __grids[id].gridView;
    		setTimeout(function() {gridView.setTopItem(index-1)}, 10);
    	} catch(e) {}
    },
    
    /*********************************************************************
     * xGrid 공통함수 마이그레이션 Start
     *********************************************************************/
    /**
     * 리얼그리드용 align 변환
     * ahLeft    = 0;
  	 * ahRight   = 1;
     * ahCenter  = 2;
     * left : near
     * center : center
     * right : far
     */
    convertHalign : function (align) {
    	//console.log("convertHalign " + align);
    	if(align == 0) {//left
    		return "near";
    	} else if(align == 1) {//right
    		return "far";
    	} else {
    		return "center";
    	}
    },
    
    /**
     * index로 컬럼을 가져온다.
     * @param grid
     * @param index
     */
    getColumn : function (grid, index) {
    	var gridView = __grids[grid].gridView;
    	var colList = gridView.getColumnNames(true);
    	
    	return colList[index];
    },
    
    /**
     * 헤더의  caption 정보로 컬럼의 index 정보를 가져온다.
     * @param grid
     * @param caption
     */
    getItemIndexFromCaption : function (grid,caption) {
    	var gridView = __grids[grid].gridView;
    	var colList = gridView.getColumnNames(true);
    	//console.log("col size " + colList.length);
    	for(var i = 0, size = colList.length; i < size; i++) {
    		var header =gridView.getColumnProperty(colList[i], "header");
    		//console.log("header " + header.text);
    		//console.log("caption " + caption);
    		if(header.text == caption) {
    			//return this.columnByName(colList[i].);zjf
    			return i;
    		}
    	}
    	return -1;
    },
    
    /**
     * 컬럼의 높이를 구한다.
     * @param grid
     */
    getHeaderHeight : function(grid) {
    	var gridView = __grids[grid].gridView;
    	var header = gridView.getHeader();
    	
    	return header.height;
    	
    },
    
    /**
     * 다중/단건 선택 여부 설정
     * mode : false : 기본 체크박스, true : 단건선택 라디오
     */
    setCheckBarExclusiveMode : function (grid, mode) {
    	var gridView = __grids[grid].gridView;
    	gridView.setCheckBar({
    		exclusive : mode
    	});
    },
    
    /**
     * Indicator 헤더와 푸터의 텍스를 설정한다.
     * @param header
     * @param footer
     */
    setIndicatorHeaderFooterText : function (grid, header, footer) {
    	var gridView = __grids[grid].gridView;
    	gridView.setIndicator({
    		headText : header,
    		footText : footer
    	});
    },
    
    getColumnCount : function(grid) {
    	var gridView = __grids[grid].gridView;
    	var colList = gridView.getColumnNames(true);
    	if(colList) {
    		return colList.length;
    	} else {
    		return 0;
    	}
    	
    },
    
    getDisplayColumns : function(grid) {
    	return __grids[grid].gridView.getDisplayColumns();
    },
    
    getDisplayColumnCount : function(grid) {
    	var colList	=  __grids[grid].gridView.getDisplayColumns();
    	if( colList ) {
    		return colList.length;
    	} else {
    		return 0;
    	}
    },
    
    getField : function(grid, index) {
    	var dataProvider 	= __grids[grid].dataProvider;
    	var fields			= dataProvider.getFields();
    	return fields[index];
    },
    
    getFieldIndex : function(grid, fieldName) {
    	var dataProvider 	= __grids[grid].dataProvider;
    	return dataProvider.getFieldIndex(fieldName);
    },
    
    getColumNames : function(grid) {
    	var gridView = __grids[grid].gridView;
    	return gridView.getColumnNames(true);
    },
    
    setIndicator : function(grid, indicator) {
    	var gridView = __grids[grid].gridView;
    	gridView.setIndicator({
    		visible : indicator
    	});
    	
    },
    
    setIndicatorWidth : function (grid, indicatorWidth, indicatorMinWidth, indicatorMaxWidth) {
    	var gridView = __grids[grid].gridView;
    	gridView.setIndicator({
    		width : indicatorWidth
    	});
    },
    
    
    /**
     * XGridObj.BeginUpdate 대응함수
     */
    beginUpdate : function (grid) {
    	var dataProvider = __grids[grid].dataProvider;
    	dataProvider.beginUpdate();
    },
    
    endUpdate : function(grid) {
    	try {
	    	var dataProvider = __grids[grid].dataProvider;
    		dataProvider.endUpdate();
    	} catch(e){}
    },
    
    getSelectedTotalRowsData : function (grid, columns, columnGubun, recordsGubun, defStr) {
    	var dataProvider 	= __grids[grid].dataProvider;
    	var gridView 		= __grids[grid].gridView;
    	var returnValue		= "";
    	
    	if( is_NullValue(defStr) ) {
    		defStr	= "";
    	}
    	
    	if( columns.substring(columns.length-1) == "|" ) {
    		columns = columns.substring(0, columns.length-1);
    	}
    	columns	= columns.split("|");
    	
    	var gridColumNames	= this.getColumNames(grid);
    	
    	var fields		= dataProvider.getFields();
    	var fieldsArray	= new Array();
    	var fieldIndex	= null;
    	$.each(columns, function(i, v) {
    		fieldIndex	= dataProvider.getFieldIndex(v);
    		fieldsArray.push(fields[fieldIndex]);
    	});
    	
    	var checkedList = gridView.getCheckedItems(false);
    	
    	
    	var gridRowCount	= this.getGridRowCount(grid);
    	for( var i = 0; i < gridRowCount; i++ ) {
    		var isExist	= false;
    		$.each(checkedList, function(idx, val){
    			if( val == i ) {
    				isExist	= true;
    				return;
    			}
    		});
			
			if( !isExist ) {
    			continue;
    		}
    		
    		var cellValue	= null;
    		for( var j = 0; j < columns.length; j++ ) {
        		isExist	= false;
        		$.each(gridColumNames, function(idx, val){
        			if( val == columns[j] ) {
        				isExist	= true;
        				return;
        			}
        		});
				
				if( isExist ) {
        			cellValue	= gridView.getValue(i, columns[j]);
    				if( is_NullValue(cellValue) ) {
        				cellValue = defStr;
    				} else {
    					if( fieldsArray[j].dataType == "datetime" ) {
        					var newDate	= new Date(cellValue);
        					cellValue	= newDate.getFullYear() + ("0"+(newDate.getMonth()+1)).slice(-2) + ("0"+(newDate.getDate())).slice(-2);
        				}
        			}    				
    			} else {
    				cellValue = defStr;
    			}
    			
    			if( j == (columns.length-1) ) {
    				returnValue	+= cellValue;
    			} else {
    				returnValue	+= cellValue + columnGubun;
    			}
    		}
    		
    		if( i < (gridRowCount-1) ) {
    			returnValue	+= recordsGubun;
    		}
    	}

    	return returnValue;
    },
    
    /******************************************************************************
    Purpose   :   columns항목들의 정보를 리턴한다.
    Usage     :   void getTotalRecordsData(grid, columns, columnGubun, recordsGubun)
    Parameter :	  -. grid : 그리드객체
  				        -. columns : 데이터를 가져올 칼럼명 리스트
  				        -. columnGubun : 칼럼간 구분자
  				        -. rowsGubun : record구분자
    Return    :   columns항목들의 정보를 리턴한다.
    Comment   :
    *****************************************************************************/
    getTotalRecordsData : function (grid, columns, columnGubun, recordsGubun, defStr) {
    	var dataProvider 	= __grids[grid].dataProvider;
    	var gridView 		= __grids[grid].gridView;
    	var returnValue		= "";
    	
    	if( is_NullValue(defStr) ) {
    		defStr	= "";
    	}
    	
    	if( columns.substring(columns.length-1) == "|" ) {
    		columns = columns.substring(0, columns.length-1);
    	}
    	columns	= columns.split("|");
    	
    	var gridColumNames	= this.getColumNames(grid);
    	
    	var fields		= dataProvider.getFields();
    	var fieldsArray	= new Array();
    	var fieldIndex	= null;
    	$.each(columns, function(i, v) {
    		fieldIndex	= dataProvider.getFieldIndex(v);
    		fieldsArray.push(fields[fieldIndex]);
    	});
    	
    	var gridRowCount	= this.getGridRowCount(grid);
    	for( var i = 0; i < gridRowCount; i++ ) {
    		var cellValue	= null;
    		for( var j = 0; j < columns.length; j++ ) {
        		var isExist	= false;
        		$.each(gridColumNames, function(idx, val){
        			if( val == columns[j] ) {
        				isExist	= true;
        				return;
        			}
        		});
    			
    			if( isExist ) {
        			cellValue	= gridView.getValue(i, columns[j]);
    				if( is_NullValue(cellValue) ) {
        				cellValue = defStr;
    				} else {
    					if( fieldsArray[j].dataType == "datetime" ) {
        					var newDate	= new Date(cellValue);
        					cellValue	= newDate.getFullYear() + ("0"+(newDate.getMonth()+1)).slice(-2) + ("0"+(newDate.getDate())).slice(-2);
        				}
        			}    				
    			} else {
    				cellValue = defStr;
    			}
    			
    			if( j == (columns.length-1) ) {
    				returnValue	+= cellValue;
    			} else {
    				returnValue	+= cellValue + columnGubun;
    			}
    		}
    		
    		if( i < (gridRowCount-1) ) {
    			returnValue	+= recordsGubun;
    		}
    	}

    	return returnValue;
    },
    
    getTotalRowsDataColFix : function(grid, columns, columnGubun, recordsGubun) {
    	return this.getTotalRecordsData(grid, columns, columnGubun, recordsGubun, " ");
    },
    
    // 그리드의 지정된 라인의 컬럼으로 포커스를 이동한다.
    setGridFocus : function (grid, idx, colName, defaultVaule) {
    	try {
    		var gridView = __grids[grid].gridView;
    		var index = {
    				itemIndex : idx,
    				column : colName
    			
    		};
    	
    		gridView.setCurrent(index);
    	} catch(e) {}
    	/**
    	if (colName == null)		colName = "선택";
    	if (defaultVaule == null)	defaultVaule = true;
    	var iCol = XObj.GetItemIndexFromCaption(colName);
    	try
    	{
    		//    		XObj.SetValue(idx, iCol, defaultVaule);
    		XObj.FocusedRecordIndex = idx;
    		XObj.FocusedItemIndex = iCol;
    		XObj.SetFocus();
    	}
    	catch (e)
    	{
    		//    		setGridFocus(XObj, idx);
    	}
    	**/
    },
    
    resetCurrent : function(id) {
    	var gridView	= __grids[id].gridView;
    	gridView.resetCurrent();
    },
    
    newLine : function (grid, iLineCount) {
    	this.commit(grid);
    	
	    var dataProvider	= __grids[grid].dataProvider;
	    if( is_NullValue(iLineCount) ) iLineCount = 10;
	    
	    this.beginUpdate(grid);
	    try {
		    var newRowData		= new Array();
		    for( var i = 0; i < iLineCount; i++ ) {
		    	newRowData.push(new Array());
		    }
		    dataProvider.insertRows(dataProvider.getRowCount(), newRowData);
	    } catch(e) {  } finally {
	    	this.endUpdate(grid);
	    }
    },

    deleteRow : function(grid, columnNm, msgFunc) {
    	this.commit(grid);
    	
    	if( is_NullValue(columnNm) )	columnNm	= "선택";
    	if( is_NullValue(msgFunc) )	msgFunc		= "uf_alert";
    	
    	var nRowCount	= this.getGridRowCount(grid);
    	if( nRowCount < 1 ) {
    		eval(msgFunc + "('삭제할 데이터가 없습니다.')");
    		return;
    	}
    	
    	if( this.getSelectedCount(grid) <= 0 ) {
    		eval(msgFunc + "('삭제할 행을 선택해 주시기 바랍니다.')");
    		return;
    	}
    	
    	if( !confirm("선택하신 행을 삭제 하시겠습니까?") ) {
    		return;
    	}
    	
		var gridView 		= __grids[grid].gridView;
		var items			= gridView.getCheckedRows(false, true);
		
		var dataProvider	= __grids[grid].dataProvider;
		dataProvider.removeRows(items, false);
   		alert("삭제 되었습니다.");
    },
    
    // 진행상태바 활성화/비활성화
    viewProgressBar : function (grid, bVisible) {
    	
    	/**
        if (bVisible)
    	{
    		XObj.GridProgressbarShow();
        }
    	else
    	{
    		XObj.GridProgressbarHide();
        }
        **/
    },
    
    newData : function (grid, iLineCount)    {
    	// grid_todo 문재균 차장
    	
    	var dataProvider = __grids[grid].dataProvider;
    	
    	var rowCount = dataProvider.getRowCount();
    	if(rowCount > 0) {
    		if (confirm("새로 만들기를 하시면 현재 화면에 입력하신 내용이 지워집니다.\n\n계속 하시겠습니까?")) {
    			dataProvider.clearRows();
    			this.newLine(grid, iLineCount);
    		}
    	} else {
    		dataProvider.clearRows();
    		this.newLine(grid, iLineCount);
    	}
    	
    	
    	
    	
    	/**
    	if (iLineCount == null || iLineCount == "")	iLineCount = 10;
    	iLineCount = iLineCount * 1;

    	clearXGrid(XObj);

    	var nRowCount = getGridRowCount(XObj);
    	if (nRowCount > 0)
    	{
    		if (confirm("새로 만들기를 하시면 현재 화면에 입력하신 내용이 지워집니다.\n\n계속 하시겠습니까?"))
    		{
    			XObj.Clear();
    			newLine(XObj, iLineCount);
    		}
    	}
    	else
    	{
    			XObj.Clear();
    			newLine(XObj, iLineCount);
    	}
    	**/
    	
    },
    
    /******************************************************************************
    Purpose   :   푸터의 합계 컬럼의 합계 값을 조회
    Usage     :   void getSummeryColumnData(grid, columnname)
    Parameter :	  -. grid : 그리드객체
  				        -. columnname : 합계가 존재하는 칼럼명
  				        -. getGubun   : 가져올 값의 구분 디폴트:'value'/'text'
    Return    :   합계정보 String
    Comment   :
    *****************************************************************************/
    getSummeryColumnData : function (grid, columnname, getGubun) {
    	var gridView = __grids[grid].gridView;
    	var summary = "" + gridView.getSummary(columnname,"sum");
    	return summary;
	    /**
    	var rtnValue = '';
	    if ((getGubun==null) || (getGubun==undefined))
	      getGubun = 'value';
	    var ColumnIndex = grid.GetItemIndexFromCaption( columnname );
	    if (ColumnIndex >= 0)
	    {
	      var SummeryIndex = grid.GetSummeryIndexFromCaption(columnname);
	      if (SummeryIndex >= 0) {
	        rtnValue = grid.GetSummeryData(ColumnIndex, SummeryIndex, getGubun);
	      }
	    }
	    return rtnValue;
	    **/
	},
    
    /******************************************************************************
    Purpose   :   컬럼의 index 속성으로 컬럼 조회
    Usage     :   String getSelectedRow(grid)
    Parameter :
    Return    :   
    Comment   :
    *****************************************************************************/
    getColumnByIndex : function (grid, index) {
    	var gridView = __grids[grid].gridView;
    	
    	//
    	var colList = gridView.getColumnNames(true);
    	
    	for(var i = 0, size = colList.length; i < size; i++) {
    		var displayIndex =gridView.getColumnProperty(colList[i], "displayIndex");
    		//console.log("header " + header.text);
    		//console.log("caption " + caption);
    		if(displayIndex == index) {
    			return gridView.columnByName(colList[i]);
    		}
    	}
    	
    },
    
    /******************************************************************************
    Purpose   :   컬럼의 순서를 이동한다.
    Usage     :   String getSelectedRow(grid)
    Parameter :
    Return    :   
    Comment   :
    *****************************************************************************/
    setColumnMoving : function (grid, AColIndex, AMoving){
    	var gridView = __grids[grid].gridView;
    	
    	//현재 컬럼인덱스로 컬럼을 찾는다.
    	var currColumn = RgUtil.getColumnByIndex(grid, AColIndex);
    	
    	//컬럼의 인덱스 속성을 변경한다.
    	gridView.setColumnProperty(currColumn, "displayIndex", AMoving);
    	
    	/**
    	if (grid.ColumnCount > AColIndex){
    		var Column = grid.GetColumn(AColIndex);
    		Column.Moving = AMoving;
    	}
    	**/
    	
    },
    
    
    /******************************************************************************
    Purpose   :   선택된 행의 키값을 불러온다.
    Usage     :   String getSelectedRow(grid)
    Parameter :
    Return    :   선택된 행의 번호
    Comment   :
    *****************************************************************************/
    getSelectedRow : function (grid)  {
    	//checkbar 선택이 아닌 현재 포커스된 레코드의 값을 가져오는 것이다.
    	var gridView = __grids[grid].gridView;
    	var focusCell = gridView.getCurrent();
        return this.getRowData(id, focusCell.dataRow);
        
	  	/**
    	var uTemp;
	  	if (grid.FocusedRecordIndex < 0) {
	  	  return "FALSE";
	  	} else {
	  	  uTemp = grid.GetValue(grid.FocusedRecordIndex, 0);
	  	  if (uTemp == null) return '';
	  	  else               return uTemp;
	  	}
	  	**/
	},
    
    getCellToView : function (grid, row, columnname){
    	/**
      var uTemp;
    	var itemIndex = grid.GetItemIndexFromCaption(columnname);
    	if((itemIndex < 0) || (row < 0)) {
    		return '';
    	} else {
    	  uTemp = grid.GetText(row, itemIndex);
    	  if ( uTemp == null ) return '';
    	  else                 return uTemp;
    	  //return rtnValue;
    		//return grid.GetValue(row, itemIndex);
    	}
    	**/
    	var gridView = __grids[grid].gridView;
    	gridView.getValue(row,columnname);
    },
    
    showProgressBar : function (grid){
    	/** 프로그레스바 표시
        grid.ProgressBarUse = true;
        grid.ProgressBarMaxUse = true;
        grid.ShowProgressBar();
        **/
    	if( checkHtml5Browser() ) {
	        var gridView = __grids[grid].gridView;
	        gridView.showProgress();
    	}
    },

    hideProgressBar : function (grid){
    	/** 프로그레스바 표시
        grid.HideProgressBar();
        grid.ProgressBarUse = false;
        grid.ProgressBarMaxUse = false;
        **/
    	if( checkHtml5Browser() ) {
	    	var gridView = __grids[grid].gridView;
	    	gridView.closeProgress();
    	}
    },
    
    checkGridValidation : function(grid, validFunc, columnNm) {
    	if( is_NullValue(validFunc) )	validFunc	= "checkRowValidation";
    	if( is_NullValue(columnNm) )		columnNm	= "선택";

    	var nRowCount	= this.getGridRowCount(grid);
    	this.setUnSelectedAll(grid, columnNm);
    	
    	var errorMessage	= null;
    	for( var i = 0; i < nRowCount; i++ ) {
    		errorMessage	= eval(validFunc + "(null, '" + grid +"', "+ i +")");
    		if( !is_NullValue(errorMessage) ) {
    			uf_alert(" * [" + (i+1) + " 라인] " + errorMessage);
    			return false;
    		}
    	}
    	    	
    	return true;
    },

    /******************************************************************************
    Purpose :   컬럼 보이기/숨기기
    Usage :     String displayColumn()
    Parameter :
    Return :     
    Comment :    
    *****************************************************************************/
    displayColumn : function (grid, colName, bVisible) {
    	var gridView = __grids[grid].gridView;
    	
    	var colunm = gridView.columnByName(colName);
    	gridView.setColumnProperty(colName, "visible", bVisible);
    	
    	/**
    	var result = false;
    	XObj.BeginUpdate();
    	try
    	{
    		var itemIndex = XObj.GetItemIndexFromCaption(colName);
    		if( (itemIndex >= 0) && (itemIndex < XObj.ColumnCount) )
    		{
    			var ColumnObj = XObj.GetColumn( itemIndex );
    		 	ColumnObj.Editing = bVisible;
    		 	ColumnObj.Visible = bVisible;
    			result = true;
    		}
    	}
    	catch (e)
    	{
    	}
    	finally
    	{
    		XObj.EndUpdate();
    	}
    	return result;
    	**/
    },
    
    displayColumnArray : function(grid, colNameArray, bVisible) {
    	var gridView	= __grids[grid].gridView;
    	
    	this.beginUpdate(grid);
    	try {
	    	$.each(colNameArray, function(k, v) {
	        	var colunm	= gridView.columnByName(k);
	        	gridView.setColumnProperty(k, "visible", 		bVisible);
	        	gridView.setColumnProperty(k, "displayIndex", 	v);
	    	});
    	} catch(e) { } finally {
    		this.endUpdate(grid);
    	}
    },
    
    /******************************************************************************
    Purpose :   컬럼 보이기/숨기기
    Usage :     String displayColumn()
    Parameter :
    Return :     
    Comment :    
    *****************************************************************************/
    displayColumn2 : function (grid, colName, bVisible) {
    	var gridView = __grids[grid].gridView;
    	
    	var colunm = gridView.getColumnByCaption(colName);
    	
    	//원 소스에 주석처리되 있음
    	//gridView.setColumnProperty(column, "visible", bVisible);
    	
    	return true;
    	
    	/**
    	var result = false;
	XObj.BeginUpdate();
	try
	{
		var itemIndex = XObj.GetItemIndexFromCaption(colName);
		if( (itemIndex >= 0) && (itemIndex < XObj.ColumnCount) )
		{
			var ColumnObj = XObj.GetColumn( itemIndex );
		//	ColumnObj.Editing = bVisible;
		//	ColumnObj.Visible = bVisible;
			result = true;
		}
	}
	catch (e)
	{
	}
	finally
	{
		XObj.EndUpdate();
	}
    	return result;
    	**/
    },
    
    /******************************************************************************
    Purpose :     선택된 행의 키값을 불러온다.
    Usage :     String getSelectedRecord()
    Parameter :
    Return :     String : 키값
    Comment :     키값은 두번째 컬럼으로써 화면에는 보이지 않으나 값은 저장되어있다.
    *****************************************************************************/
    getSelectedRecord : function (grid) {
    	//편집모드에서는 어떻게 돌까?
    	//return grid.FocusedRecordIndex;
    	var gridView = __grids[grid].gridView;
    	//var cellIndex = gridView.getCurrent();
    	var checkeckList = gridView.getCheckedItems();
    	if(checkeckList) {
    		return checkeckList[0];
    	} else {
    		return -1;
    	} 
    	
    },
    
    getSelectedRecordIndex : function(grid) {
    	var gridView	= __grids[grid].gridView;
    	var index		= gridView.getCurrent();
    	return index.dataRow;
    },
    
    /******************************************************************************
    Purpose :     한 행 전체를 삭제한다.
    Usage :     void setDeleteRecord(String key)
    Parameter :     key : 두번째 컬럼에 숨겨놓은 키값
    Return :     없음
    Comment :
    *****************************************************************************/
    setDeleteRecord : function (grid, keyidx) {
    	
    	var dataProvider = __grids[grid].dataProvider;
    	dataProvider.removeRow(keyidx);
    	/**
	  	var index = grid.GetItemIndexFromCaption('키값');
	  	if( index >= 0 ){
	  		var RecordIndex = grid.SearchRecord(index, keyidx);
	  		if( RecordIndex >= 0 ) grid.DeleteRecord( RecordIndex );
	  	}
	  	**/
    },
        
    /******************************************************************************
    Purpose :     화면 전체를 비활성화 시킨다.
    Usage :     boolean setLock()
    Parameter :
    Return :     boolean : true
    Comment :     메소드가 실행된후 true 값을 반환한다.
    *****************************************************************************/
    setUnLock : function (grid) {
    	//grid.GridEnabled = false;
    	
    	var gridView = __grids[grid].gridView;
    	gridView.setEditOptions({
    		readOnly : false,
    		editable : true
    	});
    },
        
    /******************************************************************************
    Purpose : 화면 전체를 비활성화 시킨다.
    Usage :     boolean setLock()
    Parameter :
    Return :     boolean : true
    Comment :     메소드가 실행된후 true 값을 반환한다.
    *****************************************************************************/
    setLock : function (grid) {
    	//grid.GridEnabled = false;
    	
    	var gridView = __grids[grid].gridView;
    	gridView.setEditOptions({
    		readOnly : true,
    		editable : false
    	});
    },
    
    /******************************************************************************
    Purpose   :     체크박스 전체를 체크된 상태로 바꾼다.
    Usage     :     void setFromToSelected(String FieldName, Integer nFrom, nTo)
    Parameter :     FieldName : 체크박스 필드 이름
    Return    :     없음
    Comment   :
    *****************************************************************************/
    setFromToSelected : function (grid, columnname, nFrom, nTo) {
    	
    	var gridView = __grids[grid].gridView;
    	
	    RgUtil.setUnSelectedAll(grid, columnname);
	    
	    var recordCount = RgUtil.getGridRowCount(grid);
	    if(recordCount > 0) {
	    	for(var i = nFrom; startIdx <= nTo; i++ ) {
	    		gridView.checkItem(i, true, false);
	    	}
	    }
	    /**
	  	var ColumnObj, PropObj, idx, i;
	  	if(grid.RecordCount > 0)
	  	{
	  		idx = grid.GetItemIndexFromCaption(columnname);
	  		if( (idx >= 0) && (idx < grid.ColumnCount) )
	  		{
	  			ColumnObj = grid.GetColumn( idx );
	  			if( ColumnObj.GetPropTypeName() == 'Check' )
	  			{
	  				grid.BeginUpdate();
	  				try {
	  					for(i=nFrom;i<nTo;i++)
	  						grid.SetValue(i, idx, true);
	  				} finally {
	  					grid.EndUpdate();
	  				}
	  			}
	  		}
	  	}
	  	**/
	},
    
    /******************************************************************************
    Purpose :     체크박스 전체를 체크된 상태로 바꾼다.
    Usage :     void setSelectedAll(String FieldName)
    Parameter :     FieldName : 체크박스 필드 이름
    Return :     없음
    Comment :
    *****************************************************************************/
    setSelectedAll : function (grid, columnname) {
    	//grid.SetSelectedAll(columnname);
    	var gridView = __grids[grid].gridView;
    	gridView.checkAll(true,false,false);
    },
    
    /******************************************************************************
    Purpose :     체크박스 전체를 체크되지 않은 상태로 바꾼다.
    Usage :     void setUnSelectedAll(String FieldName)
    Parameter :     FieldName : 필드 이름
    Return :     없음
    Comment :
    *****************************************************************************/
    setUnSelectedAll : function (grid, columnname) {
    	var gridView = __grids[grid].gridView;
    	gridView.checkAll(false,false,false);
    },
    
    /******************************************************************************
    Purpose :     그리드의 첫행에 표시될 Sequence No. 를 지정한다.
    Usage :     void setDefaultSeq(seq)
    Parameter :     seq : 첫행의 Sequence No.
    Return :     없음
    Comment :     첫번째 행의 번호만 지정해주면 아래행들은 1씩 자동 증가한다.
             데이터를 insert 하기전에 세팅을 해주어야 한다.
    *****************************************************************************/
    setDefaultSeq : function (grid, start)	{
    	var gridView = __grids[grid].gridView;
    	gridView.setIndicator({
    		indexOffset : start
    	});
	  	
	},
    
    /******************************************************************************
    Purpose :     그리드의 데이터를 모두 지운다.
    Usage :     void setClearGrid()
    Parameter :
    Return :     없음
    Comment :
   *****************************************************************************/
    setClearGrid : function (grid) {
    	var dataProvider = __grids[grid].dataProvider;
    	dataProvider.clearRows();
    },
    
    /**
     * 
     */
    getSubTotalOneColumnKind : function (grid, columnname, akind, adisplayformat, halign){
    	var gridView = __grids[grid].gridView;
    	
    	//컬럼을 가져온다.
		var column		= null;
		var columnArray	= columnname.split("@");
		if( columnArray.length == 2 ) {
			var parent	= gridView.columnByName(columnArray[0]);
			var child	= parent.columns;
			for( var i = 0; i < child.length; i++ ) {
				if( child[i].name == columnArray[1] ) {
					column	= child[i];
					break;
				}
			}
		} else {
			column	= gridView.columnByName(columnname);			
		}
		
    	//컬럼의 footer 정보를 설정한다.
    	var lmNumberFormat = "";
    	
    	
    	if (adisplayformat==undefined||adisplayformat==null||adisplayformat=='') {
    		lmNumberFormat = "#,##0";
    	} else {
    		lmNumberFormat = adisplayformat;
    	}
    	
    	if( is_NullValue(halign) ) { 
    		halign	= "far";
    	} else {
    		halign	= getGridAlign(halign);
    	}
    	
    	var columnFooter = {
    				styles : {
	    				textAlignment : halign,
	    				numberFormat  : lmNumberFormat
	    			},
	    			//text : "합계",
	    			expression : "sum",
	    			//todo 코드변환이 필요함
	    			//groupText : "합계",
	    			groupExpression : "sum"
    	};
    	if( akind == 1 ) {
    		columnFooter.text				= "";
    		columnFooter.styles.suffix		= "건";
    		columnFooter.expression			= "count";
    		columnFooter.groupExpression	= "count";
    	}
    	
    	// 편집그리드에서는 자동건수 Summary 수행하지 않음.
    	if( is_True(__grids[grid]["isIbkEditGrid"]) && akind == 1 ) {
    		columnFooter.text				= null;
    		columnFooter.styles.suffix		= null;
    		columnFooter.expression			= null;
    		columnFooter.groupExpression	= null;
    	}
    	
    	//console.log("akind" + akind);
    	//컬럼의 footer를 적용한다.
    	gridView.setColumnProperty(column,"footer", columnFooter);
    	
    	
    },
    
    /******************************************************************************
    Purpose :     푸터에 합계 정보 출력을 위한 caption 설정
    Usage :     void setMoveFirstRecord()
    Parameter :
    Return :     없음
    Comment :  캡션을 설정할 컬럼과 캡션명을 지정한다.
    *****************************************************************************/
    getSubTotalOneColumnCaption : function (grid, columnname, caption, halign){
    	var gridView = __grids[grid].gridView;
    	
    	//console.log("halign : " + halign);
    	//컬럼을 가져온다.
		var column		= null;
		var columnArray	= columnname.split("@");
		if( columnArray.length == 2 ) {
			var parent	= gridView.columnByName(columnArray[0]);
			var child	= parent.columns;
			for( var i = 0; i < child.length; i++ ) {
				if( child[i].name == columnArray[1] ) {
					column	= child[i];
					break;
				}
			}
		} else { 
			column	= gridView.columnByName(columnname);			
		}
    	
    	//align 변환
    	var conHalign = this.convertHalign(halign);
    	//console.log("conHalign : " + conHalign);
    	//컬럼의 footer 정보를 설정한다.
    	var columnFooter = {
    				styles : {
    					//todo 코드 변환
	    				textAlignment : conHalign
	    			},
	    			text : caption
	    };
    	
    	//JSON.stringify(this.getGridDataColumns())
    	//console.log("columnFooter" + JSON.stringify(columnFooter));
    	
    	//컬럼의 footer를 적용한다.
    	gridView.setColumnProperty(column,"footer", columnFooter);
    	
    	//gridView.setColumn(column);
    	
    	/**
    	var ColIdx = grid.GetItemIndexFromCaption( columnname );
    	if(ColIdx < 0) return;
    	var ColObj = grid.GetColumn(ColIdx );
    	var PropObj = ColObj.GetProperties();

    	if(halign  == undefined)
    		ColObj.FooterAlignHorz = PropObj.AlignHorz;
    	else
    		ColObj.FooterAlignHorz = halign;

    	ColObj.FooterKind = fkCount;
    	ColObj.FooterFormat = '"'+caption+'"';
    	**/
    },
    
     
    /******************************************************************************
    Purpose :     첫번째 행으로 이동
    Usage :     void setMoveFirstRecord()
    Parameter :
    Return :     없음
    Comment :     프로그램상에서 index 를 이동할뿐 값을 반환하지는 않는다.
   *****************************************************************************/
    setMoveFirstRecord : function (grid) {
    	var gridView = __grids[grid].gridView;
    	gridView.reSetCheckedRecordIndexIbk();
    },
    
    /******************************************************************************
    Purpose :     그리드의 전체행 수를 불러온다.
    Usage :     int getGridRowCount()
    Parameter :
    Return :     int : 전체 행 수
    Comment :
   *****************************************************************************/
    getGridRowCount : function (grid) {
    	var dataProvider = __grids[grid].dataProvider;
    	return dataProvider.getRowCount(); 	
	},
	
	getItemCount : function(grid) {
    	var gridView = __grids[grid].gridView;
    	return gridView.getItemCount();
	},
	
    /******************************************************************************
    Purpose :     체크박스에 체크된 개수를 반환한다.
    Usage :     int getSelectedCount(String FieldName)
    Parameter :     FieldName : 체크박스 필드 이름
    Return :     int : 체크된 개수
    Comment :
   *****************************************************************************/
    getSelectedCount : function (grid) {
    	var gridView = __grids[grid].gridView;
    	
    	return gridView.getCheckedItems(true).length;
	 },
     
    /******************************************************************************
    Purpose :     체크박스에 체크된 행번호를 하나씩 차례로 불러온다.
    Usage :     int getNextSelectedRecord(String FieldName)
    Parameter :     FieldName : 체크박스 필드 이름
    Return :     int : 행번호
    Comment :
   *****************************************************************************/
    getNextSelectedRecord : function (grid, columnname) {
    	var gridView = __grids[grid].gridView;
    	//@grid 아래 함수의 의미가 없을거 같은데 나중에 소스 오면 보자..
    	//var index = gridView.GetItemIndexFromCaption(columnname);
    	var ni = gridView.getNextSelectedRecordIbk("");
    	return ni;
    },
    
    getSelectedCell : function(id) {
        var gridView  = __grids[id].gridView;
        return gridView.getCurrent();
    },
    
    /******************************************************************************
 	Purpose :     특정 셀의 내용을 불러온다.
	Usage :     String getCell(int RowIndex, String FieldName)
	Parameter :	RowIndex : 그리드에서의 행번호
	FieldName : 그리드에서의 필드 이름
	            bChkCmbTxt 가 false 이면 콤보박스 선택여부 체크하지 않음
	Return :     String
	Comment :
    *****************************************************************************/
    getCell : function (id, row, columnname, bChkCmbTxt) {
    	var grid	= __grids[id].gridView;
    	var retVal	= "";
    	
    	//전체 개수를 가져오기위해서 프로바이더를 가져옴
    	var dataProvider	= __grids[id].dataProvider;
    	if( dataProvider.getRowCount() <= row ) {
    		return retVal;
    	}

    	var cellData	= grid.getValue(row, columnname);
    	if( is_NullValue(cellData) || (typeof cellData == "number" && isNaN(cellData)) ) {
    		cellData	= "";
    	} else {
        	var field	= this.getField(id, this.getFieldIndex(id, columnname));
    		if( !is_NullValue(field) ) {
	    		if( field.dataType == "datetime" ) {
		    		var newDate	= new Date(cellData);
		    		cellData	= newDate.getFullYear() + ("0"+(newDate.getMonth()+1)).slice(-2) + ("0"+(newDate.getDate())).slice(-2);
	    		}
	    	}
    	}
    	
    	return cellData;
    	
    	/*
    	//검토 사항
    	//콤보인 경우 value 값이 나옴
        return grid.getValue(row,columnname);
    	//그룹핑된 상태에서 소계를 위해 for loop 시 그룹 헤더때문에 index가 달라진다.
    	//gridView 또는 provider에서 getValue 시 원본 데이터가 나온, 포멧 형식 지정된 값이
    	//나오지는 않음
    	//return dataProvider.getValue(row,columnname);
    	*/
    },
    
    setRows : function (grid, data, startIndex, endIndex) {
    	if( is_NullValue(startIndex) )	startIndex	= 0;
    	if( is_NullValue(endIndex) )     endIndex 	= -1;
    	
    	this.beginUpdate(grid);
    	try {
	    	var dataProvider = __grids[grid].dataProvider;
	    	dataProvider.setRows(data, startIndex, endIndex);
    	} catch(e) {  } finally {
    		this.endUpdate(grid);
    	}
    },

    getDisplayValues : function(grid, rowId) {
    	var gridView = __grids[grid].gridView;
    	return gridView.getDisplayValues(rowId,true,true);
    },
    
    getDisplayValue : function(grid, rowId, colIdx) {
    	var gridView = __grids[grid].gridView;
    	var value	= "";
    	if( typeof colIdx == "string" ) {
    		
    		value	= gridView.getDisplayValues(rowId,true,true)[colIdx];
    		if(value === undefined) {
    			colIdx	= colIdx.toUpperCase();
    			value	= gridView.getDisplayValues(rowId,true,true)[colIdx];
    		}
    	} else {
    		value	= gridView.getDisplayValues(rowId,true,true)[this.getColumn(grid, colIdx)];    		
    	}
    	
    	return value;
    },
    
    getValue : function(grid, rowId, colIdx) {
    	/*
    	var gridView = __grids[grid].gridView;
    	return gridView.getValue(rowId, colIdx);
    	*/
    	var gridView	= __grids[grid].gridView;
    	var value		= gridView.getValue(rowId, colIdx);
    	if( typeof value == "undefined" ) {
    		value		= null;
    	}
    	
    	return value; 
    },
    
    getText : function(grid, rowId, columnNm) {
    	var gridView	= __grids[grid].gridView;
    	var column		= gridView.columnByField(columnNm);
        var values		= column.values;
        var labels		= column.labels;
        var label		= null;	
        
        for( var k in values ) {
            if( values[k] == gridView.getValue(rowId, columnNm) ) {
                label	= labels[k];
                break;
            }
        }
        
        return label;
    },
    
    setValue : function (grid,rowId,colIdx,value) {
    	var gridView = __grids[grid].gridView;
    	gridView.setValue(rowId, colIdx, value);
    },
    
    /******************************************************************************
      Purpose :     특정행에 배경색을 넣는다.
      Usage :     void setSpecialRowBackColor(String rgb, int nIndex)
      Parameter :	rgb : rgb 값을 예제와 같이 스트링 형태로 넣는다.
    				nIndex : 행번호
      Return :     없음
      Comment :
     *****************************************************************************/
    setSpecialRowBackColor: function (gridId, column, value, color) {
        var grid = __grids[gridId].gridView;                                                                                                          
	        //console.log("styleConfig : call" );
	        setTimeout(function() {
	        var criteriaValue;
	        
	        if(isNaN(value)) {
	        	criteriaValue = "values['" + column + "'] = '" + value + "'" ;
	        } else {
	        	criteriaValue = "values['" + column + "'] = " + value + "";
	        }
	        
	        var styleConfig = {
	            body : {
	                dynamicStyles : [{
	                    //criteria : "values['" + column + "'] = '" + value + "'",
	                	criteria : criteriaValue,
	                    styles : "background=" + color
	                }]
	            }
	        };
	        //console.dir("styleConfig : " + styleConfig);
	        grid.setStyles(styleConfig);
        }, 100);
    },
    
    /******************************************************************************
      Purpose :     특정셀의  글자색을 넣는다.
      Usage :     void setSpecialCellFontColor(gridId, column, value, color)
      Parameter :	rgb : rgb 값을 예제와 같이 스트링 형태로 넣는다.
    				nIndex : 행번호
      Return :     없음
      Comment :
     *****************************************************************************/
    setSpecialCellFontColor : function (gridId, column, value, color) {
        var grid = __grids[gridId].gridView;
        //var targetColumn = grid.columnByName(column);
        var targetColumn = grid.getColumnByCaptionIbk(column);
        var criteriaValue;
        
        if(isNaN(value)) {
        	criteriaValue = "value = '" + value +"'" ;
        } else {
        	criteriaValue = "value = " + value;
        }
        
        if(targetColumn) {
            targetColumn.dynamicStyles = [{
                    //criteria : "value = '" + value + "'",
            		criteria : criteriaValue,
                    styles : "foreground=" + color
            }];
            grid.setColumn(targetColumn);
        }
        
    },
    
    /**
     * 
     * @param grid
     * @param idx
     * @param columnname
     * @returns
     */
    setSpecialCellFontColorDefault : function (grid, idx, columnname) {
    	/**
    	if(idx <= 0) return;
    	//alert(columnname);
    	var ColumnIndex = grid.GetItemIndexFromCaption( columnname );
    	//alert(ColumnIndex);
    	if(ColumnIndex >= 0){
    		if(idx <= grid.RecordCount)
    		grid.SetCellStyleTextColorDefault(idx - 1, ColumnIndex);
    	}
    	**/
    },
    
    setDecimalPoint : function(grid, columnNm, point, fillChar) {
    	var gridView	= __grids[grid].gridView;
    	var column 		= gridView.columnByName(columnNm);
    	
    	this.beginUpdate(grid);
    	try {
	    	column.styles			= {  textAlignment : "far",          numberFormat : "#,##0.00"};
	    	column.dynamicStyles	= [{ criteria      : "value = 0.00", styles       : { numberFormat : "#,##0.00" } }];
	    	/*
	    	column.footer			= {  styles 	     : { textAlignment : "far", numberFormat : "#,##0.00" },
	    								 expression      : "sum",
	    								 groupExpression : "sum"
	    							  };
	    	*/
	    	
	    	gridView.setColumn(column);
    	} catch(e) {  } finally {
    		this.endUpdate(grid);
    	}
    },
    
    setSpecialRowColor : function(grid, index, color) {
    	var gridView	= __grids[grid].gridView;
    	
    	this.beginUpdate(grid);
    	try {
	    	if( is_NullValue(color) ) {
	    		color	= "#ffcccccc";
	    	}
	    	
	    	gridView.addCellStyle("specialRowColor_"+index, { background : color}, true);
	    	gridView.setCellStyles(index, -1, "specialRowColor_"+index);
    	} catch(e) {  } finally {
    		this.endUpdate(grid);
    	}
    },
    
    clearSpecialRowColor : function(grid, index) {
    	var gridView	= __grids[grid].gridView;
        gridView.setCellStyles(index, -1, null);
    },
    
    setRangeSpecialRowColor : function(grid, startRow, endRow, color) {
    	var gridView	= __grids[grid].gridView;

    	this.beginUpdate(grid);
    	try {
	    	var gridArray		= new Array();
	    	var gridRowCount	= this.getGridRowCount(grid);
	    	for( var i = 0; i < gridRowCount; i++ ) {
	    		gridArray.push(i);
	    	}
	        gridView.setCellStyles(gridArray, -1, null);
	    	
	    	if( is_NullValue(color) ) {
	    		color	= "#ffcccccc";
	    	}
	        
	    	var rowArray	= new Array();
	    	for( var i = startRow; i <= endRow; i++ ) {
	    		rowArray.push(i);
	    	}
	    	gridView.addCellStyle("rangeSpecialRowColor", { background : color}, true);
	    	gridView.setCellStyles(rowArray, -1, "rangeSpecialRowColor");
    	} catch(e) {  } finally {
    		this.endUpdate(grid);
    	}
    },
    
    applyCustomFilter : function (id, filterColumn, filterType, filterOper, filterValue) {
    	var gridView	= __grids[id].gridView;
        if (filterType == "value") {
            filterValue = "'" + filterValue + "'";
        } else {
            var targetField = gridView.getColumnProperty(filterColumn, "fieldName");
            filterValue = "values['" + targetField + "']";
        }

        var filters = {
            name: "custom_filter",
            criteria: "value " + filterOper + " " + filterValue,
            active: true
        };
        
        //console.log(JSON.stringify(filters));
        // 이전 customer filter를 제거
        if (_oldFilterColumn)
        	gridView.removeColumnFilters(_oldFilterColumn, "custom_filter");
        
        gridView.addColumnFilters(filterColumn, filters, true);
        
        _oldFilterColumn = filterColumn;
    },
    
    setColumnFilterData : function (id, columnName,value) {
        RgUtil.applyCustomFilter(id,columnName,"value","match",value);
    },
    
    removeColumnFilters : function(id) {
    	var gridView	= __grids[id].gridView;
    	if (_oldFilterColumn)
        	gridView.removeColumnFilters(_oldFilterColumn, "custom_filter");
    },
    
    getSuffix : function(id, columnName) {
    	var gridView	= __grids[id].gridView;
    	var columnStyle = gridView.getColumnProperty(columnName,"styles");
    	
    	if(columnStyle.suffix) {
    		return columnStyle.suffix;
    	} else {
    		return null;
    	}
    	
    },
    
    getDataType : function(id, columnName) {
    	var gridView	= __grids[id].gridView;
    	var provider    = __grids[id].dataProvider;
    	
    	var field = this.getFieldByColumnName(id, columnName);
    	
    	if(is_NullValue(field)) {
    		return "";
    	} else {
    		return field.dataType;
    	}
    	
    	
    },
    
    getFieldByColumnName : function(id, columnName) {
    	var provider    = __grids[id].dataProvider;
    	
    	var fieldList = provider.getFields();
    	for(var i = 0, cnt = fieldList.length; i < cnt; i++) {
    		var field = fieldList[i];
    		//console.log("field.fieldName " + field.fieldName);
    		if(field.fieldName === columnName) {
    			return field;
    		}
    	}
    	return null;
    },
    
    setSortingEnabled : function(id, bool) {
    	var gridView	= __grids[id].gridView;
    	var option		= {enabled : bool}; 
    	gridView.setSortingOptions(option);
    },
    
    addSearchCondition : function(id, key, value) {
    	var gridView	= __grids[id].gridView;
    	gridView.appendSearchCondition(key,value);
    },
    
    clearSearchCondition : function(id) {
    	var gridView	= __grids[id].gridView;
    	try {
    		gridView.clearSearchCondition();
    	} catch(e){}
    },
    
    changeColumnTitle : function(id, index, title) {
    	var gridView	= __grids[id].gridView;
    	
    	var colList = gridView.getDisplayColumns();
    	var column = colList[index];
    	var conf = {
    			text : title
    	};
    	gridView.setColumnProperty(column,"header", conf);
    },
    
    isEditGrid : function(id) {
    	return is_True(__grids[id]["isIbkEditGrid"]);
    },
    
    setCheckable : function(id, index, bool) {
    	var gridView	= __grids[id].gridView;
    	gridView.setCheckable(index, bool);
    }
    
    /*********************************************************************
     * xGrid 공통함수 마이그레이션 End
     *********************************************************************/

};

/**************************************************************************
 그리드 관리에 필요한 추가 구현
**************************************************************************/

RealGrids.PrintVisibility = {
	DEFAULT : "default",
	VISIBLE : "visible",
	HIDDEN  : "hidden"
};


function checkGridInit() {
	var flashGridCheck;
	//console.log("checkGridInit call");
	
	if(!checkHtml5Browser()) {
		//console.log("checkGridInit call ie8");
		if(typeof(RgUtil.getGridView("grid_area")) == undefined) {
			//console.log("checkGridInit before setTimeout");
			flashGridCheck = setTimeout("checkGridInit()",5);
			//console.log("checkGridInit after setTimeout");
		} else {
			//clear timeoutcl
			//console.log("checkGridInit grid init");
			
			clearTimeout(flashGridCheck);
			loadData();
			//console.log("checkGridInit clear init");
		}
	}
};

function uf_ShowPrint(id) {
	RgUtil.showPrint(id);
	//pcs_js_common.jsp 에 기구현된 함수는 제거
}

function uf_SaveToFilePDF(id) {
	RgUtil.saveToFilePDF(id);
}

function uf_MakeHiddenData(frm, id, fieldNm) {

	// 임시로 작성된 객체 삭제
	var elems = document.all.tags('INPUT');
	var elm   = null;
	for (var i = 0; i < elems.length ; i++){
		elm = elems[i];
		if (elm.getAttribute("_temp") != null && elm.getAttribute("_temp") == "true"){
			frm.removeChild(elm);
		}
	}
	var rowData = RgUtil.getFocusRowData(id);
    // 폼에 히든 추가
	var e;
	if (typeof(fieldNm) ==="undefined" || fieldNm == null){
		for (keyNm in rowData[0]){
			if (rowData[0].hasOwnProperty(keyNm)){
				e = document.createElement("input");
				e.setAttribute("type","hidden");
				e.setAttribute("name",keyNm);
				e.setAttribute("value",rowData[0][keyNm]);
				e.setAttribute("_temp","true");
				frm.appendChild(e);
			}
		}
	} else {
		if (rowData[0].hasOwnProperty(fieldNm)){
			e = document.createElement("input");
			e.setAttribute("type","hidden");
			e.setAttribute("name",fieldNm);
			e.setAttribute("value",rowData[0][fieldNm]);
			e.setAttribute("_temp","true");
			frm.appendChild(e);
		}
	}
}


/**
 * 레이어팝업 띄우기
 * @param obj
 * @param offset
 * @param mask
 * @returns {Boolean}
 */

function layerModalPop(obj, offset, mask){
	
	var $target = $('#'+obj);
	var obj = $('#click_'+obj);
	var $mask = '<div class="maskFilm layerMask"></div>';
	var $wh = $(window).height();
	var $ww = $(window).width();
	
	$($target).css('z-index',999990000);
	

	$(obj).addClass('on');
	$target.show().attr('tabindex', 0);

	// true : 클릭된 영역에 뜨게 함
	// false : 화면 가운대
	if(offset){

		var $parentTop = $(obj).closest('div.modal').css('top'); 
		var $parentLeft = $(obj).closest('div.modal').css('left'); 
		var $offset = $(obj).offset();
		if($parentTop != undefined){
			$($target).css({'top':$parentTop, 'left':$parentLeft});
		}else{
			$($target).css({'top':$offset.top, 'left':$offset.left});
		}
		
	}else{
		//$target.css({'top':'50%', 'left':'50%', 'marginTop': $th, 'marginLeft':$tw});
		
		if(!$('div').hasClass('layerMask')){
			$($target).css({'top':Math.max(0, (($(window).height()-$target.outerHeight())/2)+$(window).scrollTop())+'px', 'left':Math.max(0, (($(window).width()-$target.outerWidth())/2)+$(window).scrollLeft())+'px'});
			//console.log($($target).css("top"));
			//console.log($($target).css("left"));
			//console.log($($target).css("width"));
			//console.log($($target).css("z-index"));
			//console.log($($target).css("display"));
			//$($target).css({'top':'480px', 'left':'639px', 'width':'550px', 'z-index':'999999999', 'display':'block'});
		}else{
			var $wd = ($(window).width()-$(obj).closest('div.modal').width())/2;
			var $ht = ($(window).height()-$(obj).closest('div.modal').height())/2;
			var $top = $(obj).closest('div.modal').css('top').replace('px','');
			var $left = $(obj).closest('div.modal').css('left').replace('px','');
			var $scrollTop, $scrollLeft;

			$scrollTop = Math.max(0, ($top-$ht));
			$scrollLeft = Math.max(0, ($left-$wd));
			$($target).css({'top':Math.max(0, (($(window).height()-$target.outerHeight())/2)+$scrollTop)+'px', 'left':Math.max(0, (($(window).width()-$target.outerWidth())/2)+$scrollLeft)+'px'});
			//$($target).css({'top':'480px', 'left':'639px', 'width':'550px', 'z-index':'999999999', 'display':'block'});
		}

	}

	// true : mask on
	// false : mask none
	if(mask){
		if(!$('div').hasClass('layerMask')){
			$('body').css({'position':'fixed','overflow-y':'scroll','margin-top':-($(window).scrollTop())});
			
			$target.after($mask).next('div.layerMask').show();

		}
		
	}

	$target.focus();


	$target.find('.btns_close > a, a.layer_close, a.layer_confirm').click(function(){
		if(mask){
			$($target).next('div.layerMask').remove('.layerMask');
			if(!$('div').hasClass('layerMask')){
				$('body').removeAttr('style');
			}
		}
		$(obj).removeClass('on');
		$target.hide();
		$('#'+$(this).attr('href').replace('#','')).focus();
		$target.removeAttr('tabindex');

		return false;
	});
	return false;
	
	//$("#" + obj).trigger('custom_open_layer');
	
}

var swfLoadcheck;
function swfCallBackTest(id, callback) {
	
	//swfLoadcheck = setInterval(isLoad(id), 1);
	
	//swfLoadcheck = setInterval(function() {isLoad(id,callback);}, 1);
	//swfLoadcheck = setInterval(function() {isLoad(id,loadData);}, 1);
}

function isLoad(id, callback) {
	var gridView = RgUtil.getGridView(id);
	var dataProvider = RgUtil.getdataProvider(id);
	if(typeof(gridView) === undefined || dataProvider === undefined) {
		//console.log("not yet......")
	} else {
		//console.log("loaded !! ======================");
		clearInterval(swfLoadcheck);
		if(callback) {
			//console.log("loaded !! ======================");
			callback();
		}
		//console.log("clearInerval !! ======================");
	}
}

function toSplit(row, delimeter) {

	var colList = [];
	
	var offset = 0;
	var index = 0;
	var value = null;
	
	
	do {
		index = row.indexOf(delimeter,offset);
		if(index == -1) {
			break;
		}
		
		value = row.substring(offset,index);
		value = value == null ? "" : $.trim(value);
		offset = index + delimeter.length
		colList.push(value);
		
			
	} while(index != -1);
    
	value = row.substring(offset);
	value = value == null ? "" : $.trim(value);
	colList.push(value)
	
	return colList;
};

function is_NullValue(_val) {
	if( _val === undefined || _val === null || _val === "" ) {
		return true;
	}
	
	return false;
}

//그리드 컬럼 중복데이터 관련 처리
var DupDeTector = function(id) {
	this.gridId					= id;
	this.dupDataArray			= new Array();
	this.dupColumnCaption		= "";
	this.columncaptionDelimeter	= "|";
	
	this.includeFirstRow		= true;
	this.columnDelimeter		= "";
	this.rowDelimeter			= "";	
}

DupDeTector.prototype			= {
		
	setDuplicationColumnCaption : function(caption) {
		this.dupColumnCaption	= caption;
	},
	
	setIncludeFirstRow : function(includeFirstRow) {
		this.includeFirstRow	= includeFirstRow;
	},
	
	execute : function() {
		var gridDataString	= RgUtil.getTotalRecordsData(this.gridId, this.dupColumnCaption, this.columnDelimeter, this.rowDelimeter);
		if( gridDataString.substring(gridDataString.length - 1) == this.rowDelimeter ) {
			gridDataString	= gridDataString.substring(0, gridDataString.length - 1)
		}
		
		var dataArray		= new Array();
		
    	var rowsArray		= gridDataString.split(this.rowDelimeter);
    	for( var i = 0; i < rowsArray.length; i++ ) {
    		if( !is_NullValue(rowsArray[i]) ) {
				var tmpData	= rowsArray[i].split(this.columnDelimeter)[0];
				tmpData		= tmpData.replace(/ /gi, "");
				dataArray.push(tmpData);
    		}
    	}

		var stdData	= null;
		var cprData	= null;
    	for( var i = 0; i < dataArray.length; i++ ) {
    		var isExist	= false;
    		$.each(this.dupDataArray, function(idx, val){
    			if( val == i ) {
    				isExist	= true;
    				return;
    			}
    		});
    		
    		if( isExist ) {
    			continue;
    		}
    		
    		stdData	= dataArray[i];
    		for( var j = i + 1; j < dataArray.length; j++ ) {
        		isExist	= false;
        		$.each(this.dupDataArray, function(idx, val){
        			if( val == j ) {
        				isExist	= true;
        				return;
        			}
        		});
    			
	    		if( isExist ) {
	    			continue;
	    		}
    			
    			cprData	= dataArray[j];
    			if( stdData == cprData ) {
            		isExist	= false;
            		$.each(this.dupDataArray, function(idx, val){
            			if( val == i ) {
            				isExist	= true;
            				return;
            			}
            		});
    				
   					if( !isExist ) {
   						this.dupDataArray.push(i);
   					}

   	        		isExist	= false;
   	        		$.each(this.dupDataArray, function(idx, val){
   	        			if( val == j ) {
   	        				isExist	= true;
   	        				return;
   	        			}
   	        		});
   					
    				if( !isExist ) {
    					this.dupDataArray.push(j);
    				}
    			}
    		}
    	}
    	
	},
	
	getResultIndexArray : function() {	
		return this.dupDataArray;
	}
	
}

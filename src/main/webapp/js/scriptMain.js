function addMapping() {
	if ($("#new-iri").val() == '' || $("#old-iri").val() == '') {
		alert('empty input');
	} else {
		var index = $("#mapping").length-1;
		var oldIRI = $("#old-iri").val();
		var newIRI = $("#new-iri").val();
		$("#mapping").append(
			'<tr><td style="text-align:center;width:45%"">' +
			oldIRI +
			'</td><td style="text-align:center;width:45%">' +
			newIRI +
			'</td><td><input type="hidden" name="hashMap[' +
			oldIRI +
			']" value="' +
			newIRI +
			'"/><input type="button" class="delete" value="Del" onclick="removeMapping($(this))"/></td></tr>'
		);
		$("#new-iri").val('');
		$("#old-iri").val('');
	}
}

function removeMapping(item) {
	item.parent('td').parent('tr').remove();
}

function exampleMapping() {
	var oldIRI = 'http://www.ifomis.org/bfo/1.1#Entity';
	var newIRI = 'http://purl.obolibrary.org/obo/BFO_0000001';
	$("#mapping").append(
		'<tr><td style="text-align:center;width:45%"">' +
		oldIRI +
		'</td><td style="text-align:center;width:45%">' +
		newIRI +
		'</td><td><input type="hidden" name="hashMap[' +
		oldIRI +
		']" value="' +
		newIRI +
		'"/><input type="button" class="delete" value="Del" onclick="removeMapping($(this))"/></td></tr>'
	);
}

function getCleaningField( action ) {
	var actionField = '';
	switch (action) {
		case 'add':
			actionField = 'hashAddNamespace';
			break;
		case 'remove':
			actionField = 'hashRemoveNamespace';
			break;
		case 'replace':
			actionField = 'hashReplaceNamespace';
			break;
		default:
			alert('unknown action');
	}
	return actionField;
}

function addCleaning() {
	if ($("#clean-iri").val() == '' || $("#clean-action").val() == '' || $("#clean-type").val() == '' ) {
		alert('empty input');
	} else {
		var index = $("#cleaning").length-1;
		var type = $("#clean-type").val();
		var action = $("#clean-action").val();
		var actionField = getCleaningField( action );

		var iri = $("#clean-iri").val();
		
		$("#cleaning").append(
			'<tr><td style="text-align:center;width:22.5%">' +
			action +
			'</td><td style="text-align:center;width:22.5%">' +
			type +
			'</td><td style="text-align:center;width:45%">' +
			iri +
			'</td><input type="hidden" name="' +
			actionField +
			'[' +
			iri +
			']" value="' +
			type +
			'"/><td><input type="button" class="delete" value="Del" onclick="removeCleaning($(this))"/></td></tr>'
		);
		$("#clean-iri").val('');
	}
}

function removeCleaning(item) {
	item.parent('td').parent('tr').remove();
}

function exampleCleaning() {
	var type = 'xmlns';
	var action = 'add';
	var actionField = getCleaningField( action );
	var iri = 'http://purl.obolibrary.org/obo/';
	$("#cleaning").append(
		'<tr><td style="text-align:center;width:22.5%">' +
		action +
		'</td><td style="text-align:center;width:22.5%">' +
		type +
		'</td><td style="text-align:center;width:45%">' +
		iri +
		'</td><input type="hidden" name="' +
		actionField +
		'[' +
		iri +
		']" value="' +
		type +
		'"/><td><input type="button" class="delete" value="Del" onclick="removeCleaning($(this))"/></td></tr>'
	);
	
	var type = 'xmlns';
	var action = 'remove';
	var actionField = getCleaningField( action );
	var iri = 'http://www.ifomis.org/bfo/1.1#';
	$("#cleaning").append(
		'<tr><td style="text-align:center;width:22.5%">' +
		action +
		'</td><td style="text-align:center;width:22.5%">' +
		type +
		'</td><td style="text-align:center;width:45%">' +
		iri +
		'</td><input type="hidden" name="' +
		actionField +
		'[' +
		iri +
		']" value="' +
		type +
		'"/><td><input type="button" class="delete" value="Del" onclick="removeCleaning($(this))"/></td></tr>'
	);
	
	var type = 'import';
	var action = 'replace';
	var actionField = getCleaningField( action );
	var iri = 'http://www.ifomis.org/bfo/1.1;http://purl.obolibrary.org/obo/bfo/2014-05-03/classes-only.owl';
	$("#cleaning").append(
		'<tr><td style="text-align:center;width:22.5%">' +
		action +
		'</td><td style="text-align:center;width:22.5%">' +
		type +
		'</td><td style="text-align:center;width:45%">' +
		iri +
		'</td><input type="hidden" name="' +
		actionField +
		'[' +
		iri +
		']" value="' +
		type +
		'"/><td><input type="button" class="delete" value="Del" onclick="removeCleaning($(this))"/></td></tr>'
	);
}
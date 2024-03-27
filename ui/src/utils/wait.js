export default (millis) => {
	return new Promise((resolve) => {
		setTimeout(resolve, millis);
	});
};

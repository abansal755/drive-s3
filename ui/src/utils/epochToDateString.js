const epochToDateString = (epoch) => {
	const date = new Date(epoch);
	return `${date.toLocaleTimeString()}, ${date.toLocaleDateString()}`;
};

export default epochToDateString;

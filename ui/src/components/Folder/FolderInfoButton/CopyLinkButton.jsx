import { Button } from "@chakra-ui/react";
import {
	Text,
	CheckCircleIcon,
	CopyIcon,
} from "../../common/framerMotionWrappers";
import { useCopyToClipboard } from "@uidotdev/usehooks";
import { Fragment, useEffect, useState } from "react";

const Icon = ({ isClicked }) => {
	return (
		<Fragment>
			{isClicked && (
				<CheckCircleIcon
					key="check-circle-icon"
					initial={{ opacity: 0, scale: 0 }}
					animate={{ opacity: 1, scale: 1 }}
					exit={{ opacity: 0, scale: 0 }}
				/>
			)}
			{!isClicked && (
				<CopyIcon
					initial={{ opacity: 0, scale: 0 }}
					animate={{ opacity: 1, scale: 1 }}
					exit={{ opacity: 0, scale: 0 }}
				/>
			)}
		</Fragment>
	);
};

const CopyLinkButton = ({ copyValue }) => {
	const copyToClipboard = useCopyToClipboard()[1];
	const [isClicked, setIsClicked] = useState(false);

	useEffect(() => {
		if (isClicked) setTimeout(() => setIsClicked(false), 3000);
	}, [isClicked]);

	const btnClickHandler = () => {
		copyToClipboard(copyValue);
		setIsClicked(true);
	};

	return (
		<Button
			colorScheme={isClicked ? "green" : "blue"}
			rightIcon={<Icon isClicked={isClicked} />}
			mr={2}
			onClick={btnClickHandler}
			pr={6}
		>
			{isClicked && (
				<Text
					initial={{ opacity: 0, scale: 0 }}
					animate={{ opacity: 1, scale: 1 }}
					exit={{ opacity: 0, scale: 0 }}
				>
					Copied
				</Text>
			)}
			{!isClicked && (
				<Text
					initial={{ opacity: 0, scale: 0 }}
					animate={{ opacity: 1, scale: 1 }}
					exit={{ opacity: 0, scale: 0 }}
				>
					Copy
				</Text>
			)}
			<Text ml={1}>Link</Text>
		</Button>
	);
};

export default CopyLinkButton;

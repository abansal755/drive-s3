import { IconButton, Tooltip } from "@chakra-ui/react";
import {
	TriangleUpIcon,
	TriangleDownIcon,
} from "../../../../common/framerMotionWrappers";
import { Fragment } from "react";

const framerProps = {
	initial: { opacity: 0, scale: 0 },
	animate: { opacity: 1, scale: 1 },
	exit: { opacity: 0, scale: 0 },
};

const Icon = ({ permissionType }) => {
	return (
		<Fragment>
			{permissionType === "READ" && <TriangleUpIcon {...framerProps} />}
			{permissionType === "WRITE" && (
				<TriangleDownIcon {...framerProps} />
			)}
		</Fragment>
	);
};

const ModifyPermissionButton = ({
	permissionType,
	modifyPermissionMutation,
	deletePermissionMutation,
}) => {
	const iconBtnClickHandler = () => {
		if (permissionType === "READ") modifyPermissionMutation.mutate("WRITE");
		if (permissionType === "WRITE") modifyPermissionMutation.mutate("READ");
	};

	return (
		<Tooltip
			label={
				permissionType === "READ"
					? "Upgrade permission to WRITE"
					: "Downgrade permission to READ"
			}
		>
			<IconButton
				icon={<Icon permissionType={permissionType} />}
				isRound
				size="sm"
				colorScheme="purple"
				onClick={iconBtnClickHandler}
				isLoading={modifyPermissionMutation.isPending}
				isDisabled={deletePermissionMutation.isPending}
			/>
		</Tooltip>
	);
};

export default ModifyPermissionButton;

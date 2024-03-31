import {
	Button as ChakraButton,
	Text as ChakraText,
	HStack as ChakraHStack,
	Badge as ChakraBadge,
	IconButton as ChakraIconButton,
	Alert as ChakraAlert,
	VStack as ChakraVStack,
} from "@chakra-ui/react";
import {
	CheckCircleIcon as ChakraCheckCircleIcon,
	ExternalLinkIcon as ChakraExternalLinkIcon,
	CopyIcon as ChakraCopyIcon,
	TriangleUpIcon as ChakraTriangleUpIcon,
	TriangleDownIcon as ChakraTriangleDownIcon,
} from "@chakra-ui/icons";
import { motion } from "framer-motion";

export const Button = motion(ChakraButton);
export const Text = motion(ChakraText);
export const CheckCircleIcon = motion(ChakraCheckCircleIcon);
export const ExternalLinkIcon = motion(ChakraExternalLinkIcon);
export const CopyIcon = motion(ChakraCopyIcon);
export const HStack = motion(ChakraHStack);
export const Badge = motion(ChakraBadge);
export const IconButton = motion(ChakraIconButton);
export const TriangleUpIcon = motion(ChakraTriangleUpIcon);
export const TriangleDownIcon = motion(ChakraTriangleDownIcon);
export const Alert = motion(ChakraAlert);
export const VStack = motion(ChakraVStack);
